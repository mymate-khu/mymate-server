package com.mymate.mymate.common.exception.chat.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum ChatSuccessStatus implements SuccessResponse {

    CHAT_ROOM_CREATED(HttpStatus.CREATED, "CHAT2001", "채팅방이 생성되었습니다."),
    CHAT_ROOM_LIST_FOUND(HttpStatus.OK, "CHAT2002", "채팅방 목록을 조회했습니다."),
    CHAT_ROOM_FOUND(HttpStatus.OK, "CHAT2003", "채팅방을 조회했습니다."),
    CHAT_ROOM_LEFT(HttpStatus.OK, "CHAT2004", "채팅방에서 나갔습니다."),
    MESSAGE_LIST_FOUND(HttpStatus.OK, "CHAT2005", "메시지 목록을 조회했습니다."),
    MESSAGE_SENT(HttpStatus.CREATED, "CHAT2006", "메시지가 전송되었습니다."),
    MESSAGE_DELETED(HttpStatus.OK, "CHAT2007", "메시지가 삭제되었습니다."),
    MESSAGE_READ(HttpStatus.OK, "CHAT2008", "메시지를 읽음 처리했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ChatSuccessStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getSuccessStatus() { return httpStatus; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}