package com.mymate.mymate.notification.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mymate.mymate.notification.enums.NotificationStatus;
import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private NotificationType type;
    private NotificationStatus status;
    private Priority priority;
    private Long senderId;
    private String senderName;
    private String relatedEntityType;
    private Long relatedEntityId;
    private List<Action> actions;
    private String navigationUrl;
    private LocalDateTime readAt;
    private LocalDateTime navigatedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    public static class Action {
        private String type;
        private String label;
        private String style;
        private String apiUrl;
        private String method;
    }
}


