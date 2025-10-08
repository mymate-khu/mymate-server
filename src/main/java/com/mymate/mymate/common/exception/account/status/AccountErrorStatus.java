package com.mymate.mymate.common.exception.account.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum AccountErrorStatus implements ErrorResponse {

    // 정산
    @ExplainError("정산을 찾을 수 없음")
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT4001", "정산을 찾을 수 없습니다."),

    @ExplainError("이미 완료된 정산")
    ACCOUNT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "ACCOUNT4002", "이미 완료된 정산입니다."),

    @ExplainError("정산 접근 권한이 없음")
    ACCOUNT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCOUNT4003", "정산에 접근할 권한이 없습니다."),

    @ExplainError("정산 수정 권한이 없음")
    ACCOUNT_UPDATE_DENIED(HttpStatus.FORBIDDEN, "ACCOUNT4004", "정산을 수정할 권한이 없습니다."),

    @ExplainError("정산 삭제 권한이 없음")
    ACCOUNT_DELETE_DENIED(HttpStatus.FORBIDDEN, "ACCOUNT4005", "정산을 삭제할 권한이 없습니다."),

    // 정산 참여자
    @ExplainError("정산 참여자를 찾을 수 없음")
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT4010", "정산 참여자를 찾을 수 없습니다."),

    @ExplainError("이미 정산에 참여 중")
    PARTICIPANT_ALREADY_EXISTS(HttpStatus.CONFLICT, "ACCOUNT4011", "이미 정산에 참여하고 있습니다."),

    @ExplainError("최소 1명의 참여자가 필요")
    MINIMUM_PARTICIPANTS_REQUIRED(HttpStatus.BAD_REQUEST, "ACCOUNT4012", "최소 1명의 참여자가 필요합니다."),

    // 금액 관련
    @ExplainError("유효하지 않은 금액")
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "ACCOUNT4020", "유효하지 않은 금액입니다."),

    @ExplainError("받을 금액이 총 금액을 초과")
    RECEIVE_AMOUNT_EXCEEDS_TOTAL(HttpStatus.BAD_REQUEST, "ACCOUNT4021", "받을 금액이 총 금액을 초과할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AccountErrorStatus(HttpStatus httpStatus, String code, String message) {
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