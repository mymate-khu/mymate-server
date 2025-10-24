package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 그룹 초대 이벤트
 */
@Getter
public class GroupInvitationEvent extends NotificationEvent {
    
    private final String groupName;
    private final String inviterName;
    
    public GroupInvitationEvent(Long inviteeId, Long inviterId, String groupName, String inviterName) {
        super(
            inviteeId, 
            inviterId, 
            NotificationType.GROUP_INVITATION_RECEIVED,
            "그룹 초대가 도착했습니다",
            inviterName + "님이 '" + groupName + "' 그룹에 초대했습니다.",
            Priority.HIGH,
            "/groups/invitations",
            Map.of("groupName", groupName, "inviterName", inviterName)
        );
        this.groupName = groupName;
        this.inviterName = inviterName;
    }
}
