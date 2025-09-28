package com.mymate.mymate.puzzle.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum PuzzleSuccessStatus implements SuccessResponse {

    @ExplainError("퍼즐 생성 성공")
    PUZZLE_CREATED(HttpStatus.CREATED, "PUZZLE2001", "퍼즐이 생성되었습니다."),
    
    @ExplainError("퍼즐 조회 성공")
    PUZZLE_FOUND(HttpStatus.OK, "PUZZLE2002", "퍼즐을 조회했습니다."),
    
    @ExplainError("퍼즐 수정 성공")
    PUZZLE_UPDATED(HttpStatus.OK, "PUZZLE2003", "퍼즐이 수정되었습니다."),
    
    @ExplainError("퍼즐 삭제 성공")
    PUZZLE_DELETED(HttpStatus.OK, "PUZZLE2004", "퍼즐이 삭제되었습니다."),
    
    @ExplainError("퍼즐 완료 처리 성공")
    PUZZLE_COMPLETED(HttpStatus.OK, "PUZZLE2005", "퍼즐이 완료되었습니다."),
    
    @ExplainError("퍼즐 미완료 처리 성공")
    PUZZLE_INCOMPLETED(HttpStatus.OK, "PUZZLE2006", "퍼즐이 미완료로 변경되었습니다."),
    
    @ExplainError("퍼즐 목록 조회 성공")
    PUZZLES_FOUND(HttpStatus.OK, "PUZZLE2007", "퍼즐 목록을 조회했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PuzzleSuccessStatus(HttpStatus httpStatus, String code, String message) {
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
