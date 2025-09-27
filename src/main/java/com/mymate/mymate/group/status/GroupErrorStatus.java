package com.mymate.mymate.group.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum GroupErrorStatus implements ErrorResponse {

    // 그룹
    @ExplainError("그룹을 찾을 수 없음")
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP4001", "그룹을 찾을 수 없습니다."),
    
    @ExplainError("이미 그룹에 속한 멤버")
    MEMBER_ALREADY_IN_GROUP(HttpStatus.CONFLICT, "GROUP4002", "이미 그룹에 속한 멤버입니다."),
    
    @ExplainError("그룹에 속하지 않은 멤버")
    MEMBER_NOT_IN_GROUP(HttpStatus.BAD_REQUEST, "GROUP4003", "그룹에 속하지 않은 멤버입니다."),
    
    @ExplainError("그룹 소유자는 제거할 수 없음")
    CANNOT_REMOVE_OWNER(HttpStatus.BAD_REQUEST, "GROUP4004", "그룹 소유자는 제거할 수 없습니다."),

    // 초대
    @ExplainError("초대를 찾을 수 없음")
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP4010", "초대를 찾을 수 없습니다."),
    
    @ExplainError("이미 대기 중인 초대")
    INVITATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "GROUP4011", "이미 대기 중인 초대가 있습니다."),
    
    @ExplainError("이미 처리된 초대")
    INVITATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "GROUP4012", "이미 처리된 초대입니다."),
    
    @ExplainError("만료된 초대")
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "GROUP4013", "만료된 초대입니다."),

    // 권한
    @ExplainError("접근 권한이 없음")
    FORBIDDEN(HttpStatus.FORBIDDEN, "GROUP4030", "접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    GroupErrorStatus(HttpStatus httpStatus, String code, String message) {
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
