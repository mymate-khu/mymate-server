package com.mymate.mymate.chat.dto;

import com.mymate.mymate.chat.entity.ChatRoom;
import com.mymate.mymate.chat.enums.ChatRoomType;

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
@Schema(description = "채팅방 응답")
public class ChatRoomResponse {

    @Schema(description = "채팅방 ID", example = "1")
    private Long id;

    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;

    @Schema(description = "채팅방 이름", example = "우리 가족")
    private String name;

    @Schema(description = "채팅방 타입", example = "GROUP")
    private ChatRoomType type;

    @Schema(description = "활성 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "참여자 수", example = "5")
    private Integer participantCount;

    @Schema(description = "읽지 않은 메시지 수", example = "3")
    private Integer unreadCount;

    @Schema(description = "마지막 메시지", example = "안녕하세요!")
    private String lastMessage;

    @Schema(description = "마지막 메시지 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime lastMessageAt;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom, Integer participantCount,
                                        Integer unreadCount, String lastMessage,
                                        LocalDateTime lastMessageAt) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .groupId(chatRoom.getGroupId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .isActive(chatRoom.getIsActive())
                .participantCount(participantCount)
                .unreadCount(unreadCount)
                .lastMessage(lastMessage)
                .lastMessageAt(lastMessageAt)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}