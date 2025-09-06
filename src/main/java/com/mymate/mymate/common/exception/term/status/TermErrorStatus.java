package com.mymate.mymate.common.exception.term.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum TermErrorStatus implements ErrorResponse {

    @ExplainError("약관을 찾을 수 없음")
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM4001", "약관을 찾을 수 없습니다."),

    @ExplainError("최신 약관 버전 아님")
    NOT_LATEST_TERM_VERSION(HttpStatus.BAD_REQUEST, "TERM4002", "최신 약관 버전이 아닙니다."),

    @ExplainError("필수 약관 동의 누락")
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST, "TERM4003", "필수 약관 동의가 누락되었습니다."),

    @ExplainError("약관 동의 저장 실패")
    AGREEMENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TERM5001", "약관 동의 저장에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TermErrorStatus(HttpStatus httpStatus, String code, String message) {
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


