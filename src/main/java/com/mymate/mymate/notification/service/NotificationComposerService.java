package com.mymate.mymate.notification.service;

import java.util.List;
import java.util.Map;

import com.mymate.mymate.notification.entity.Notification;
import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

public interface NotificationComposerService {

    Notification composeAndSend(Long recipientId,
                                Long senderId,
                                NotificationType type,
                                String title,
                                String content,
                                Priority priority,
                                String navigationUrl,
                                Map<String, Object> data);

    List<Notification> composeAndSendToUsers(List<Long> recipientIds,
                                             NotificationType type,
                                             String title,
                                             String content,
                                             Priority priority,
                                             String navigationUrl,
                                             Map<String, Object> data);
}


