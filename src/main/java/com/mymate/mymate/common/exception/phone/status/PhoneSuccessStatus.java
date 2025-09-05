package com.mymate.mymate.common.exception.phone.status;

import org.springframework.http.HttpStatus;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;

public enum PhoneSuccessStatus implements SuccessResponse {

    // 휴대폰 인증 관련
    VERIFICATION_CODE_SENT(HttpStatus.OK, "PHONE2000", "휴대폰 인증번호가 발송되었습니다."),
    VERIFICATION_SUCCESS(HttpStatus.OK, "PHONE2001", "휴대폰 인증이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PhoneSuccessStatus(HttpStatus httpStatus, String code, String message) {
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
