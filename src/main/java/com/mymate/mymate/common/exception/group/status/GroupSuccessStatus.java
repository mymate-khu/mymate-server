package com.mymate.mymate.common.exception.group.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum GroupSuccessStatus implements SuccessResponse {

    // 그룹 관련
    GROUP_CREATED(HttpStatus.CREATED, "GROUP2000", "그룹이 성공적으로 생성되었습니다."),
    GROUP_JOINED(HttpStatus.OK, "GROUP2001", "그룹에 성공적으로 참여했습니다."),
    GROUP_LEFT(HttpStatus.OK, "GROUP2002", "그룹에서 성공적으로 탈퇴했습니다."),
    GROUP_MEMBER_ADDED(HttpStatus.OK, "GROUP2003", "그룹에 멤버를 성공적으로 추가했습니다."),
    GROUP_MEMBER_REMOVED(HttpStatus.OK, "GROUP2004", "그룹에서 멤버를 성공적으로 제거했습니다."),
    GROUP_LIST_FETCHED(HttpStatus.OK, "GROUP2005", "그룹 목록을 성공적으로 조회했습니다."),
    
    // 초대 관련
    INVITATION_CREATED(HttpStatus.CREATED, "GROUP2010", "초대가 성공적으로 생성되었습니다."),
    INVITATION_ACCEPTED(HttpStatus.OK, "GROUP2011", "초대를 성공적으로 수락했습니다."),
    INVITATION_CANCELLED(HttpStatus.OK, "GROUP2012", "초대를 성공적으로 취소했습니다."),
    INVITATION_LIST_FETCHED(HttpStatus.OK, "GROUP2013", "초대 목록을 성공적으로 조회했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    GroupSuccessStatus(HttpStatus httpStatus, String code, String message) {
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
