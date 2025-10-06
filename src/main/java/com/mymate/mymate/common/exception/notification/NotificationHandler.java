package com.mymate.mymate.common.exception.notification;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.notification.status.NotificationErrorStatus;

public class NotificationHandler extends GeneralException {

    public NotificationHandler(NotificationErrorStatus status) {
        super(status);
    }
}


