package com.mymate.mymate.puzzle;

import com.mymate.mymate.common.exception.ExceptionAdvice;
import com.mymate.mymate.common.exception.general.status.ErrorStatus;
import com.mymate.mymate.puzzle.status.PuzzleErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class PuzzleHandler extends ExceptionAdvice {

    protected void logException(Exception exception, ErrorStatus errorStatus) {
        log.error("Puzzle 도메인 예외 발생: {}", exception.getMessage(), exception);
    }

    protected void logException(Exception exception, PuzzleErrorStatus errorStatus) {
        log.error("Puzzle 도메인 예외 발생: {}", exception.getMessage(), exception);
    }
}
