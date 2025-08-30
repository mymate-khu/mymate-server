package com.mymate.mymate.common.exception.member;


import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;

public class MemberHandler extends GeneralException {
    public MemberHandler(MemberErrorStatus status) {
        super(status);
    }
}
