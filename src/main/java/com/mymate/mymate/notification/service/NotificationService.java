package com.mymate.mymate.notification.service;

import com.mymate.mymate.notification.dto.NotificationListResponse;
import com.mymate.mymate.notification.dto.NotificationResponse;

public interface NotificationService {

    NotificationListResponse getNotifications(Long memberId);

    NotificationResponse getNotification(Long notificationId, Long memberId);

    long getUnreadCount(Long memberId);

    void markAsRead(Long notificationId, Long memberId);

    int markAllAsRead(Long memberId);

    NotificationResponse navigate(Long notificationId, Long memberId);
}


