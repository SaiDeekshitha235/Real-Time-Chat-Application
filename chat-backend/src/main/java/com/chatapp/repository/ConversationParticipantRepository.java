package com.chatapp.repository;

import com.chatapp.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    @Query("SELECT cp.user.name FROM ConversationParticipant cp " +
            "WHERE cp.conversation.id = :conversationId")
    List<String> findParticipantNames(@Param("conversationId") Long conversationId);

    List<ConversationParticipant> findByConversationId(Long conversationId);

    boolean existsByConversationIdAndUserId(Long conversationId, Long userId);
}