package com.mymate.mymate.common.exception.term;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;

public class TermHandler extends GeneralException {
    public TermHandler(ErrorResponse status) {
        super(status);
    }
}


