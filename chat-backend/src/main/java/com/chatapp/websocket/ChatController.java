package com.chatapp.websocket;

import com.chatapp.dto.MessageDTO;
import com.chatapp.dto.UserDTO;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/message/{conversationId}")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        // Save message to database
        MessageDTO savedMessage = messageService.createMessage(
                chatMessage.getConversationId(),
                chatMessage.getSenderId(),
                chatMessage.getType(),
                chatMessage.getContent()
        );

        // Broadcast to conversation topic
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + chatMessage.getConversationId(),
                savedMessage
        );
    }
}