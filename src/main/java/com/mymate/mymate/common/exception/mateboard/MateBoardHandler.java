package com.mymate.mymate.common.exception.mateboard;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.mateboard.status.MateBoardErrorStatus;

public class MateBoardHandler extends GeneralException {
    public MateBoardHandler(MateBoardErrorStatus status) {
        super(status);
    }
}