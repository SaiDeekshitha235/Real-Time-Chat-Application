package com.chatapp.service;

import com.chatapp.dto.ConversationDTO;
import com.chatapp.entity.Conversation;
import com.chatapp.entity.ConversationParticipant;
import com.chatapp.entity.User;
import com.chatapp.repository.ConversationRepository;
import com.chatapp.repository.ConversationParticipantRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<ConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findUserConversations(userId);

        return conversations.stream()
                .map(conv -> buildConversationDTO(conv, userId))
                .collect(Collectors.toList());
    }

    public ConversationDTO getOrCreateOneToOneConversation(Long userId1, Long userId2) {
        // Check if conversation already exists
        Optional<Conversation> existing = conversationRepository
                .findOneToOneConversation(userId1, userId2);

        if (existing.isPresent()) {
            return buildConversationDTO(existing.get(), userId1);  // ← convert to DTO
        }

        // Create new conversation
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation = Conversation.builder()
                .type("one_to_one")
                .name(user2.getName())
                .createdBy(user1)
                .build();

        Conversation saved = conversationRepository.save(conversation);

        // Add participants
        addParticipant(saved.getId(), userId1);
        addParticipant(saved.getId(), userId2);

        return buildConversationDTO(saved, userId1);  // ← convert to DTO
    }

    public ConversationDTO createGroupConversation(String name, Long createdBy, List<Long> memberIds) {
        User creator = userRepository.findById(createdBy)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation = Conversation.builder()
                .type("group")
                .name(name)
                .createdBy(creator)
                .build();

        Conversation saved = conversationRepository.save(conversation);

        // Add creator as participant
        addParticipant(saved.getId(), createdBy);

        // Add other members
        for (Long memberId : memberIds) {
            if (!memberId.equals(createdBy)) {
                addParticipant(saved.getId(), memberId);
            }
        }

        return buildConversationDTO(saved, createdBy);  // ← convert to DTO
    }

    public void addParticipant(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            ConversationParticipant participant = ConversationParticipant.builder()
                    .conversation(conversation)
                    .user(user)
                    .build();
            participantRepository.save(participant);
        }
    }

    private ConversationDTO buildConversationDTO(Conversation conv, Long userId) {
        Long unreadCount = messageRepository.countUnreadMessages(conv.getId(), userId);
        List<String> participantNames = participantRepository.findParticipantNames(conv.getId());

        return ConversationDTO.builder()
                .id(conv.getId())
                .type(conv.getType())
                .name(conv.getName())
                .avatarUrl(conv.getAvatarUrl())
                .lastMessage(conv.getLastMessage())
                .lastMessageTime(conv.getLastMessageTime())
                .unreadCount(unreadCount)
                .participantNames(participantNames)
                .createdAt(conv.getCreatedAt())
                .build();
    }
}