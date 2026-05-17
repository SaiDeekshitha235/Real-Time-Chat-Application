package com.chatapp.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private Long id;
    private String type;
    private String name;
    private String avatarUrl;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
    private List<String> participantNames;
    private LocalDateTime createdAt;
}