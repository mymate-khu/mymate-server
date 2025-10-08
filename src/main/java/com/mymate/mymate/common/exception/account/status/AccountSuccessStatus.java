package com.mymate.mymate.common.exception.account.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum AccountSuccessStatus implements SuccessResponse {

    ACCOUNT_CREATED(HttpStatus.CREATED, "ACCOUNT2000", "정산이 성공적으로 생성되었습니다."),
    ACCOUNT_FOUND(HttpStatus.OK, "ACCOUNT2001", "정산을 성공적으로 조회했습니다."),
    ACCOUNT_LIST_FOUND(HttpStatus.OK, "ACCOUNT2002", "정산 목록을 성공적으로 조회했습니다."),
    ACCOUNT_UPDATED(HttpStatus.OK, "ACCOUNT2003", "정산이 성공적으로 수정되었습니다."),
    ACCOUNT_DELETED(HttpStatus.OK, "ACCOUNT2004", "정산이 성공적으로 삭제되었습니다."),
    ACCOUNT_STATUS_UPDATED(HttpStatus.OK, "ACCOUNT2005", "정산 상태가 성공적으로 변경되었습니다."),
    CATEGORIES_FOUND(HttpStatus.OK, "ACCOUNT2006", "카테고리 목록을 성공적으로 조회했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AccountSuccessStatus(HttpStatus httpStatus, String code, String message) {
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