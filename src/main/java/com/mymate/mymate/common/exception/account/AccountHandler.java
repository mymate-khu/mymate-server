package com.mymate.mymate.common.exception.account;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.account.status.AccountErrorStatus;

public class AccountHandler extends GeneralException {
    public AccountHandler(AccountErrorStatus status) {
        super(status);
    }
}