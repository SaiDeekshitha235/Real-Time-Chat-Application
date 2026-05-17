package com.chatapp.controller;

import com.chatapp.dto.MessageDTO;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;  // NEW

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDTO>> getMessages(
            @PathVariable Long conversationId) {
        try {
            List<MessageDTO> messages = messageService
                    .getConversationMessages(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(
            @RequestHeader("X-User-Id") Long senderId,
            @RequestBody SendMessageRequest request) {
        try {
            MessageDTO message = messageService.createMessage(
                    request.getConversationId(),
                    senderId,
                    request.getType(),
                    request.getContent()
            );

            // NEW: Broadcast to all subscribers of this conversation
            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + request.getConversationId(),
                    message
            );

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            messageService.markAsRead(id);
            return ResponseEntity.ok(new SuccessResponse("Marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/conversation/{conversationId}/read-all")
    public ResponseEntity<?> markConversationAsRead(
            @PathVariable Long conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            messageService.markConversationAsRead(conversationId, userId);
            return ResponseEntity.ok(new SuccessResponse("Conversation marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class SendMessageRequest {
        private Long conversationId;
        private String type;
        private String content;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String message;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class SuccessResponse {
        private String message;
    }
}