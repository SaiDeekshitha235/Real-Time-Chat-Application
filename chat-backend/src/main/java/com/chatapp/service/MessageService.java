package com.chatapp.service;

import com.chatapp.dto.MessageDTO;
import com.chatapp.entity.Conversation;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.repository.ConversationRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public MessageDTO createMessage(Long conversationId, Long senderId,
                                    String type, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .type(type)
                .content(content)
                .build();

        Message saved = messageRepository.save(message);

        // Update conversation's last message
        conversation.setLastMessage(content);
        conversation.setLastMessageTime(saved.getCreatedAt());
        conversationRepository.save(conversation);

        return convertToDTO(saved);
    }

    public List<MessageDTO> getConversationMessages(Long conversationId) {
        List<Message> messages = messageRepository
                .findByConversationIdOrderByCreatedAtDesc(conversationId);

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getIsRead()) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    public void markConversationAsRead(Long conversationId, Long userId) {
        List<Message> unreadMessages = messageRepository
                .findByConversationIdOrderByCreatedAtDesc(conversationId);

        for (Message message : unreadMessages) {
            if (!message.getIsRead() && !message.getSender().getId().equals(userId)) {
                message.setIsRead(true);
                messageRepository.save(message);
            }
        }
    }

    private MessageDTO convertToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .senderAvatarUrl(message.getSender().getAvatarUrl())
                .type(message.getType())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}