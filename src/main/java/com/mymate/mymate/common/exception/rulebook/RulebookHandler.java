package com.mymate.mymate.common.exception.rulebook;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.rulebook.status.RulebookErrorStatus;

public class RulebookHandler extends GeneralException {
    public RulebookHandler(RulebookErrorStatus status) {
        super(status);
    }
}