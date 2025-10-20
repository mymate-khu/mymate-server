package com.mymate.mymate.chat.service;

import com.mymate.mymate.chat.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    // 채팅방 관리
    ChatRoomResponse createChatRoom(Long memberId, ChatRoomCreateRequest request);
    List<ChatRoomResponse> getMyChatRooms(Long memberId);
    ChatRoomResponse getChatRoom(Long memberId, Long chatRoomId);
    void leaveChatRoom(Long memberId, Long chatRoomId);

    // 메시지 관리
    ChatMessageResponse sendMessage(Long memberId, ChatMessageRequest request);
    Page<ChatMessageResponse> getMessages(Long memberId, Long chatRoomId, Pageable pageable);
    void deleteMessage(Long memberId, Long messageId);
    void markAsRead(Long memberId, Long chatRoomId);
}