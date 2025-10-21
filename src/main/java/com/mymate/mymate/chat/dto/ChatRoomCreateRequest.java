package com.mymate.mymate.chat.dto;

import com.mymate.mymate.chat.enums.ChatRoomType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 생성 요청")
public class ChatRoomCreateRequest {

    @NotBlank
    @Schema(description = "채팅방 이름", example = "우리 가족")
    private String name;

    @NotNull
    @Schema(description = "채팅방 타입", example = "GROUP")
    private ChatRoomType type;
}