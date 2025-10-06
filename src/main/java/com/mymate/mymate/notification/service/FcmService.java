package com.mymate.mymate.notification.service;

import com.mymate.mymate.notification.dto.FcmMessageResponse;

import java.util.List;

public interface FcmService {

    FcmMessageResponse sendToDevice(String token, String title, String body, Object data);

    FcmMessageResponse sendToMultipleDevices(List<String> tokens, String title, String body, Object data);

    FcmMessageResponse sendToUser(Long memberId, String title, String body, Object data);

    FcmMessageResponse sendToUsers(List<Long> memberIds, String title, String body, Object data);

    FcmMessageResponse sendToTopic(String topic, String title, String body, Object data);
}


