package com.mymate.mymate.common.exception.mateboard.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum MateBoardSuccessStatus implements SuccessResponse {

    MATEBOARD_CREATED(HttpStatus.CREATED, "MATEBOARD2000", "메이트보드가 성공적으로 생성되었습니다."),
    MATEBOARD_FOUND(HttpStatus.OK, "MATEBOARD2001", "메이트보드를 성공적으로 조회했습니다."),
    MATEBOARD_LIST_FOUND(HttpStatus.OK, "MATEBOARD2002", "메이트보드 목록을 성공적으로 조회했습니다."),
    MATEBOARD_UPDATED(HttpStatus.OK, "MATEBOARD2003", "메이트보드가 성공적으로 수정되었습니다."),
    MATEBOARD_DELETED(HttpStatus.OK, "MATEBOARD2004", "메이트보드가 성공적으로 삭제되었습니다."),
    EXPIRED_MATEBOARDS_CLEANED(HttpStatus.OK, "MATEBOARD2005", "만료된 메이트보드가 정리되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MateBoardSuccessStatus(HttpStatus httpStatus, String code, String message) {
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