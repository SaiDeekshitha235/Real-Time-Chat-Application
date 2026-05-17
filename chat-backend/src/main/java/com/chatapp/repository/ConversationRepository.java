package com.chatapp.repository;

import com.chatapp.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT DISTINCT c FROM Conversation c " +
            "JOIN ConversationParticipant cp ON c.id = cp.conversation.id " +
            "WHERE cp.user.id = :userId " +
            "ORDER BY c.lastMessageTime DESC")
    List<Conversation> findUserConversations(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c " +
            "JOIN ConversationParticipant cp1 ON c.id = cp1.conversation.id " +
            "JOIN ConversationParticipant cp2 ON c.id = cp2.conversation.id " +
            "WHERE c.type = 'one_to_one' " +
            "AND cp1.user.id = :userId1 " +
            "AND cp2.user.id = :userId2")
    Optional<Conversation> findOneToOneConversation(@Param("userId1") Long userId1,
                                                    @Param("userId2") Long userId2);
}