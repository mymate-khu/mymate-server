package com.mymate.mymate.notification.mapper;

import java.util.List;

import com.mymate.mymate.notification.dto.NotificationListResponse;
import com.mymate.mymate.notification.dto.NotificationResponse;
import com.mymate.mymate.notification.entity.Notification;

public final class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .status(n.getStatus())
                .priority(n.getPriority())
                .senderId(n.getSender() != null ? n.getSender().getId() : null)
                .senderName(n.getSender() != null ? n.getSender().getUsername() : null)
                .relatedEntityType(n.getRelatedEntityType())
                .relatedEntityId(n.getRelatedEntityId())
                .navigationUrl(n.getNavigationUrl())
                .readAt(n.getReadAt())
                .navigatedAt(n.getNavigatedAt())
                .expiresAt(n.getExpiresAt())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }

    public static NotificationListResponse toListResponse(List<Notification> list) {
        List<NotificationResponse> content = list.stream().map(NotificationMapper::toResponse).toList();
        return NotificationListResponse.builder().content(content).build();
    }
}


