package com.mymate.mymate.notification.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationListResponse {
    private List<NotificationResponse> content;
}


