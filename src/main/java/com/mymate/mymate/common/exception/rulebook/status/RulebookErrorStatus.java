package com.mymate.mymate.common.exception.rulebook.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum RulebookErrorStatus implements ErrorResponse {

    @ExplainError("룰북을 찾을 수 없음")
    RULEBOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "RULEBOOK4001", "룰북을 찾을 수 없습니다."),

    @ExplainError("룰북 접근 권한이 없음")
    RULEBOOK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "RULEBOOK4002", "룰북에 접근할 권한이 없습니다."),

    @ExplainError("룰북 수정 권한이 없음")
    RULEBOOK_UPDATE_DENIED(HttpStatus.FORBIDDEN, "RULEBOOK4003", "룰북을 수정할 권한이 없습니다."),

    @ExplainError("룰북 삭제 권한이 없음")
    RULEBOOK_DELETE_DENIED(HttpStatus.FORBIDDEN, "RULEBOOK4004", "룰북을 삭제할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    RulebookErrorStatus(HttpStatus httpStatus, String code, String message) {
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