package com.mymate.mymate.notification.service;

import java.util.List;

import com.mymate.mymate.notification.dto.FcmMessageResponse;

public interface PushService {

    FcmMessageResponse sendToUser(Long memberId, String title, String body, Object data);

    FcmMessageResponse sendToUsers(List<Long> memberIds, String title, String body, Object data);
}


