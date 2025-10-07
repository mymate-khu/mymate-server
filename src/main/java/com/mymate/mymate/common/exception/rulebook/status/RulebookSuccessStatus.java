package com.mymate.mymate.common.exception.rulebook.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum RulebookSuccessStatus implements SuccessResponse {

    RULEBOOK_CREATED(HttpStatus.CREATED, "RULEBOOK2000", "룰북이 성공적으로 생성되었습니다."),
    RULEBOOK_FOUND(HttpStatus.OK, "RULEBOOK2001", "룰북을 성공적으로 조회했습니다."),
    RULEBOOK_LIST_FOUND(HttpStatus.OK, "RULEBOOK2002", "룰북 목록을 성공적으로 조회했습니다."),
    RULEBOOK_UPDATED(HttpStatus.OK, "RULEBOOK2003", "룰북이 성공적으로 수정되었습니다."),
    RULEBOOK_DELETED(HttpStatus.OK, "RULEBOOK2004", "룰북이 성공적으로 삭제되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    RulebookSuccessStatus(HttpStatus httpStatus, String code, String message) {
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