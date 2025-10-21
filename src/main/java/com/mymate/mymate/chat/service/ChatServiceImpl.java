package com.mymate.mymate.chat.service;

import com.mymate.mymate.chat.dto.*;
import com.mymate.mymate.chat.entity.ChatMessage;
import com.mymate.mymate.chat.entity.ChatParticipant;
import com.mymate.mymate.chat.entity.ChatRoom;
import com.mymate.mymate.chat.repository.ChatMessageRepository;
import com.mymate.mymate.chat.repository.ChatParticipantRepository;
import com.mymate.mymate.chat.repository.ChatRoomRepository;
import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.general.status.ErrorStatus;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ChatRoomResponse createChatRoom(Long memberId, ChatRoomCreateRequest request) {
        // 사용자의 그룹 확인
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        if (groupMembers.isEmpty()) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        Long groupId = groupMembers.get(0).getGroupId();

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .groupId(groupId)
                .name(request.getName())
                .type(request.getType())
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);

        // 그룹의 모든 멤버를 참여자로 추가
        List<GroupMember> allGroupMembers = groupMemberRepository.findByGroupId(groupId);
        for (GroupMember gm : allGroupMembers) {
            ChatParticipant participant = ChatParticipant.builder()
                    .chatRoomId(chatRoom.getId())
                    .memberId(gm.getMemberId())
                    .build();
            chatParticipantRepository.save(participant);
        }

        log.info("CHAT:CREATE:::chatRoomId={}, groupId={}, createdBy={}", chatRoom.getId(), groupId, memberId);

        return ChatRoomResponse.from(chatRoom, allGroupMembers.size(), 0, null, null);
    }

    @Override
    public List<ChatRoomResponse> getMyChatRooms(Long memberId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByMemberIdAndIsActiveTrue(memberId);

        return participants.stream()
                .map(participant -> {
                    ChatRoom chatRoom = chatRoomRepository.findById(participant.getChatRoomId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

                    Integer participantCount = chatParticipantRepository.countByChatRoomIdAndIsActiveTrue(chatRoom.getId());
                    Integer unreadCount = chatMessageRepository.countUnreadMessages(
                            chatRoom.getId(),
                            participant.getLastReadAt() != null ? participant.getLastReadAt() : chatRoom.getCreatedAt()
                    );

                    ChatMessage lastMessage = chatMessageRepository.findLatestMessageByChatRoomId(chatRoom.getId());

                    return ChatRoomResponse.from(
                            chatRoom,
                            participantCount,
                            unreadCount,
                            lastMessage != null ? lastMessage.getContent() : null,
                            lastMessage != null ? lastMessage.getCreatedAt() : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public ChatRoomResponse getChatRoom(Long memberId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndIsActiveTrue(chatRoomId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        // 참여자 확인
        chatParticipantRepository.findByChatRoomIdAndMemberIdAndIsActiveTrue(chatRoomId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FORBIDDEN));

        Integer participantCount = chatParticipantRepository.countByChatRoomIdAndIsActiveTrue(chatRoomId);
        ChatMessage lastMessage = chatMessageRepository.findLatestMessageByChatRoomId(chatRoomId);

        return ChatRoomResponse.from(
                chatRoom,
                participantCount,
                0,
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessage != null ? lastMessage.getCreatedAt() : null
        );
    }

    @Override
    @Transactional
    public void leaveChatRoom(Long memberId, Long chatRoomId) {
        ChatParticipant participant = chatParticipantRepository
                .findByChatRoomIdAndMemberIdAndIsActiveTrue(chatRoomId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        participant.leave();
        chatParticipantRepository.save(participant);

        log.info("CHAT:LEAVE:::chatRoomId={}, memberId={}", chatRoomId, memberId);
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long memberId, ChatMessageRequest request) {
        // 참여자 확인
        chatParticipantRepository.findByChatRoomIdAndMemberIdAndIsActiveTrue(request.getChatRoomId(), memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FORBIDDEN));

        // 메시지 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoomId(request.getChatRoomId())
                .senderId(memberId)
                .messageType(request.getMessageType())
                .content(request.getContent())
                .build();

        message = chatMessageRepository.save(message);

        // 발신자 정보 조회
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        ChatMessageResponse response = ChatMessageResponse.from(message, sender.getUsername());

        // WebSocket으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + request.getChatRoomId(), response);

        log.info("CHAT:MESSAGE:SENT:::chatRoomId={}, senderId={}, messageId={}",
                request.getChatRoomId(), memberId, message.getId());

        return response;
    }

    @Override
    public Page<ChatMessageResponse> getMessages(Long memberId, Long chatRoomId, Pageable pageable) {
        // 참여자 확인
        chatParticipantRepository.findByChatRoomIdAndMemberIdAndIsActiveTrue(chatRoomId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FORBIDDEN));

        Page<ChatMessage> messages = chatMessageRepository
                .findByChatRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(chatRoomId, pageable);

        return messages.map(message -> {
            Member sender = memberRepository.findById(message.getSenderId())
                    .orElse(null);
            String senderName = sender != null ? sender.getUsername() : "Unknown";
            return ChatMessageResponse.from(message, senderName);
        });
    }

    @Override
    @Transactional
    public void deleteMessage(Long memberId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        // 본인의 메시지인지 확인
        if (!message.getSenderId().equals(memberId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        message.delete();
        chatMessageRepository.save(message);

        // WebSocket으로 삭제 알림 전송
        ChatMessageResponse response = ChatMessageResponse.from(message, "");
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatRoomId() + "/delete", response);

        log.info("CHAT:MESSAGE:DELETED:::messageId={}, memberId={}", messageId, memberId);
    }

    @Override
    @Transactional
    public void markAsRead(Long memberId, Long chatRoomId) {
        ChatParticipant participant = chatParticipantRepository
                .findByChatRoomIdAndMemberIdAndIsActiveTrue(chatRoomId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        participant.updateLastReadAt();
        chatParticipantRepository.save(participant);

        log.info("CHAT:READ:::chatRoomId={}, memberId={}", chatRoomId, memberId);
    }
}