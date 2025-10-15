package com.mymate.mymate.common.exception.puzzle;

import com.mymate.mymate.common.exception.ExceptionAdvice;
import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import com.mymate.mymate.common.exception.general.status.ErrorStatus;
import com.mymate.mymate.common.exception.puzzle.status.PuzzleErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public class PuzzleHandler extends GeneralException {

public PuzzleHandler(ErrorResponse status) {super(status);}
}
