package com.mymate.mymate.chat.repository;

import com.mymate.mymate.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByGroupIdAndIsActiveTrue(Long groupId);

    Optional<ChatRoom> findByIdAndIsActiveTrue(Long id);
}