package com.mymate.mymate.common.exception.term.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum TermSuccessStatus implements SuccessResponse {

    AGREEMENT_SAVED(HttpStatus.OK, "TERM2001", "약관 동의가 저장되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TermSuccessStatus(HttpStatus httpStatus, String code, String message) {
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


