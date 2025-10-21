package com.mymate.mymate.chat.repository;

import com.mymate.mymate.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoomIdAndIsActiveTrue(Long chatRoomId);

    Optional<ChatParticipant> findByChatRoomIdAndMemberIdAndIsActiveTrue(Long chatRoomId, Long memberId);

    List<ChatParticipant> findByMemberIdAndIsActiveTrue(Long memberId);

    Integer countByChatRoomIdAndIsActiveTrue(Long chatRoomId);
}