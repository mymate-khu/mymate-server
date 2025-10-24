package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 채팅 메시지 이벤트
 */
@Getter
public class ChatMessageEvent extends NotificationEvent {
    
    private final String message;
    private final Long groupId;
    private final String senderName;
    
    public ChatMessageEvent(Long groupId, Long senderId, String senderName, String message) {
        super(
            null, // 그룹 멤버들에게 발송
            senderId,
            NotificationType.COMMENT_ADDED,
            "새로운 메시지가 도착했습니다",
            senderName + ": " + (message.length() > 50 ? message.substring(0, 50) + "..." : message),
            Priority.LOW,
            "/chat/" + groupId,
            Map.of("message", message, "groupId", groupId, "senderName", senderName)
        );
        this.message = message;
        this.groupId = groupId;
        this.senderName = senderName;
    }
}
