package com.chatapp.repository;

import com.chatapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "ORDER BY m.createdAt DESC " +
            "LIMIT 50")
    List<Message> findRecentMessages(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "AND m.isRead = false " +
            "AND m.sender.id != :userId")
    Long countUnreadMessages(@Param("conversationId") Long conversationId,
                             @Param("userId") Long userId);

    List<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId);
}