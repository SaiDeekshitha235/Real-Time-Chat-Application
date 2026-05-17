package com.chatapp.controller;

import com.chatapp.dto.ConversationDTO;
import com.chatapp.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/my-conversations")
    public ResponseEntity<List<ConversationDTO>> getMyConversations(
            @RequestHeader("X-User-Id") Long userId) {
        try {
            List<ConversationDTO> conversations = conversationService
                    .getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/one-to-one")
    public ResponseEntity<ConversationDTO> createOrGetOneToOne(    // ← changed
                                                                   @RequestHeader("X-User-Id") Long userId,
                                                                   @RequestBody Map<String, Long> body) {
        try {
            Long otherUserId = body.get("userId");
            ConversationDTO conversation = conversationService     // ← changed
                    .getOrCreateOneToOneConversation(userId, otherUserId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/group")
    public ResponseEntity<ConversationDTO> createGroup(            // ← changed
                                                                   @RequestHeader("X-User-Id") Long userId,
                                                                   @RequestBody CreateGroupRequest request) {
        try {
            ConversationDTO conversation = conversationService     // ← changed
                    .createGroupConversation(request.getName(), userId, request.getMemberIds());
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/participants/{userId}")
    public ResponseEntity<?> addParticipant(@PathVariable Long id,
                                            @PathVariable Long userId) {
        try {
            conversationService.addParticipant(id, userId);
            return ResponseEntity.ok(new SuccessResponse("Participant added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class CreateGroupRequest {
        private String name;
        private List<Long> memberIds;
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