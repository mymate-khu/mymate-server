package com.mymate.mymate.common.exception.chat.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum ChatErrorStatus implements ErrorResponse {

    @ExplainError("채팅방을 찾을 수 없음")
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4001", "채팅방을 찾을 수 없습니다."),

    @ExplainError("채팅방 접근 권한이 없음")
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT4002", "채팅방에 접근할 권한이 없습니다."),

    @ExplainError("메시지를 찾을 수 없음")
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4003", "메시지를 찾을 수 없습니다."),

    @ExplainError("메시지 삭제 권한이 없음")
    MESSAGE_DELETE_DENIED(HttpStatus.FORBIDDEN, "CHAT4004", "메시지를 삭제할 권한이 없습니다."),

    @ExplainError("이미 참여중인 채팅방")
    ALREADY_PARTICIPANT(HttpStatus.CONFLICT, "CHAT4005", "이미 참여중인 채팅방입니다."),

    @ExplainError("비활성화된 채팅방")
    CHAT_ROOM_INACTIVE(HttpStatus.BAD_REQUEST, "CHAT4006", "비활성화된 채팅방입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ChatErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getErrorStatus() { return httpStatus; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}