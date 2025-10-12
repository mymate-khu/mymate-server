package com.mymate.mymate.common.exception.mateboard.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum MateBoardErrorStatus implements ErrorResponse {

    // 메이트보드
    @ExplainError("메이트보드를 찾을 수 없음")
    MATEBOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "MATEBOARD4001", "메이트보드를 찾을 수 없습니다."),

    @ExplainError("만료된 메이트보드")
    MATEBOARD_EXPIRED(HttpStatus.BAD_REQUEST, "MATEBOARD4002", "만료된 메이트보드입니다."),

    @ExplainError("메이트보드 수정 권한이 없음")
    MATEBOARD_UPDATE_DENIED(HttpStatus.FORBIDDEN, "MATEBOARD4003", "메이트보드를 수정할 권한이 없습니다."),

    @ExplainError("메이트보드 삭제 권한이 없음")
    MATEBOARD_DELETE_DENIED(HttpStatus.FORBIDDEN, "MATEBOARD4004", "메이트보드를 삭제할 권한이 없습니다."),

    @ExplainError("메이트보드 접근 권한이 없음")
    MATEBOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MATEBOARD4005", "메이트보드에 접근할 권한이 없습니다."),

    @ExplainError("유효하지 않은 내용")
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "MATEBOARD4006", "유효하지 않은 내용입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MateBoardErrorStatus(HttpStatus httpStatus, String code, String message) {
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