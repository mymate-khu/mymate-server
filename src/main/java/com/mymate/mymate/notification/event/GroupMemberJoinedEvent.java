package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 그룹 멤버 가입 이벤트
 */
@Getter
public class GroupMemberJoinedEvent extends NotificationEvent {
    
    private final String groupName;
    private final String memberName;
    
    public GroupMemberJoinedEvent(Long groupId, Long newMemberId, String groupName, String memberName) {
        super(
            null, // 그룹 멤버들에게 발송
            newMemberId,
            NotificationType.GROUP_MEMBER_JOINED,
            "새로운 멤버가 그룹에 가입했습니다",
            memberName + "님이 '" + groupName + "' 그룹에 가입했습니다.",
            Priority.MEDIUM,
            "/groups/" + groupId,
            Map.of("groupName", groupName, "memberName", memberName, "groupId", groupId)
        );
        this.groupName = groupName;
        this.memberName = memberName;
    }
}
