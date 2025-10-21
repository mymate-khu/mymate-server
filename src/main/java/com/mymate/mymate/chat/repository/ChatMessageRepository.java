package com.mymate.mymate.chat.repository;

import com.mymate.mymate.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoomId = :chatRoomId AND m.createdAt > :lastReadAt AND m.isDeleted = false")
    Integer countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("lastReadAt") LocalDateTime lastReadAt);

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoomId = :chatRoomId AND m.isDeleted = false ORDER BY m.createdAt DESC LIMIT 1")
    ChatMessage findLatestMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}