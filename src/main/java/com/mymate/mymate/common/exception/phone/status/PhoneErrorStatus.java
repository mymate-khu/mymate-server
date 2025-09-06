package com.mymate.mymate.common.exception.phone.status;

import org.springframework.http.HttpStatus;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;

public enum PhoneErrorStatus implements ErrorResponse {

    @ExplainError("유효하지 않은 인증번호")
    VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "PHONE4000", "인증번호가 올바르지 않습니다."),

    @ExplainError("인증되지 않은 전화번호")
    NOT_VERIFIED(HttpStatus.FORBIDDEN, "PHONE4001", "휴대폰 인증이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PhoneErrorStatus(HttpStatus httpStatus, String code, String message) {
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


