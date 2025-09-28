package com.mymate.mymate.puzzle.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum PuzzleErrorStatus implements ErrorResponse {

    // 퍼즐
    @ExplainError("퍼즐을 찾을 수 없음")
    PUZZLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PUZZLE4001", "퍼즐을 찾을 수 없습니다."),
    
    @ExplainError("이미 완료된 퍼즐")
    PUZZLE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "PUZZLE4002", "이미 완료된 퍼즐입니다."),
    
    @ExplainError("아직 완료되지 않은 퍼즐")
    PUZZLE_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "PUZZLE4003", "아직 완료되지 않은 퍼즐입니다."),
    
    @ExplainError("잘못된 반복 설정")
    INVALID_RECURRENCE_SETTING(HttpStatus.BAD_REQUEST, "PUZZLE4004", "잘못된 반복 설정입니다."),
    
    @ExplainError("반복 종료일이 시작일보다 이전")
    INVALID_RECURRENCE_END_DATE(HttpStatus.BAD_REQUEST, "PUZZLE4005", "반복 종료일이 시작일보다 이전입니다."),

    // 권한
    @ExplainError("퍼즐 접근 권한이 없음")
    FORBIDDEN(HttpStatus.FORBIDDEN, "PUZZLE4030", "퍼즐에 접근할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PuzzleErrorStatus(HttpStatus httpStatus, String code, String message) {
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
