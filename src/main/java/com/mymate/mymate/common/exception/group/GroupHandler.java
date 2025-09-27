package com.mymate.mymate.common.exception.group;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.group.status.GroupErrorStatus;

public class GroupHandler extends GeneralException {
    public GroupHandler(GroupErrorStatus status) {
        super(status);
    }
}
