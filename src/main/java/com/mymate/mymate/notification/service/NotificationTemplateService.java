package com.mymate.mymate.notification.service;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;

public interface NotificationTemplateService {

    TemplateResult render(NotificationType type, Map<String, Object> variables);

    record TemplateResult(String title, String content, String navigationUrl) {}
}


