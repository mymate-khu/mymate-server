package com.mymate.mymate.common.exception.token;


import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.token.status.TokenErrorStatus;

public class TokenHandler extends GeneralException {
    public TokenHandler(TokenErrorStatus status) {
        super(status);
    }
}
