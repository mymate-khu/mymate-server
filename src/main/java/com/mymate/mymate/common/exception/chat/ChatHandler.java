package com.mymate.mymate.common.exception.chat;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.chat.status.ChatErrorStatus;

public class ChatHandler extends GeneralException {
    public ChatHandler(ChatErrorStatus status) {
        super(status);
    }
}