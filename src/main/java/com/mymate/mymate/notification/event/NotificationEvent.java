package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 알림 이벤트의 기본 클래스
 */
@Getter
public abstract class NotificationEvent {
    
    protected final Long recipientId;
    protected final Long senderId;
    protected final NotificationType type;
    protected final String title;
    protected final String content;
    protected final Priority priority;
    protected final String navigationUrl;
    protected final Map<String, Object> data;
    
    public NotificationEvent(Long recipientId, Long senderId, NotificationType type,
                           String title, String content, Priority priority,
                           String navigationUrl, Map<String, Object> data) {
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.navigationUrl = navigationUrl;
        this.data = data;
    }
}
