package com.mymate.mymate.chat.dto;

import com.mymate.mymate.chat.entity.ChatMessage;
import com.mymate.mymate.chat.enums.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "채팅 메시지 응답")
public class ChatMessageResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "발신자 ID", example = "123")
    private Long senderId;

    @Schema(description = "발신자 이름", example = "홍길동")
    private String senderName;

    @Schema(description = "메시지 타입", example = "TEXT")
    private MessageType messageType;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    public static ChatMessageResponse from(ChatMessage message, String senderName) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isDeleted(message.getIsDeleted())
                .build();
    }
}