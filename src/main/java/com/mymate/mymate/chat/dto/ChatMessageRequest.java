package com.mymate.mymate.chat.dto;

import com.mymate.mymate.chat.enums.MessageType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "채팅 메시지 전송 요청")
public class ChatMessageRequest {

    @NotNull
    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @NotNull
    @Schema(description = "메시지 타입", example = "TEXT")
    private MessageType messageType;

    @NotBlank
    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
}