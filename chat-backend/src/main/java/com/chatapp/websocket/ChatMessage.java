package com.chatapp.websocket;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String content;
    private String type;
    private String timestamp;
    private MessageType messageType;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}