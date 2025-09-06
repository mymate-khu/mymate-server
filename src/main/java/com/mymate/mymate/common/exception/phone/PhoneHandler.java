package com.mymate.mymate.common.exception.phone;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;

public class PhoneHandler extends GeneralException {
    public PhoneHandler(ErrorResponse status) {
        super(status);
    }
}


