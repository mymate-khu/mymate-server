package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 정산 생성 이벤트
 */
@Getter
public class SettlementCreatedEvent extends NotificationEvent {
    
    private final String settlementTitle;
    private final Long groupId;
    private final String creatorName;
    private final Double amount;
    
    public SettlementCreatedEvent(Long groupId, Long creatorId, String settlementTitle, String creatorName, Double amount) {
        super(
            null, // 그룹 멤버들에게 발송
            creatorId,
            NotificationType.SETTLEMENT_CREATED,
            "새로운 정산이 생성되었습니다",
            creatorName + "님이 '" + settlementTitle + "' 정산을 생성했습니다. (금액: " + amount + "원)",
            Priority.HIGH,
            "/settlements/" + groupId,
            Map.of("settlementTitle", settlementTitle, "groupId", groupId, "creatorName", creatorName, "amount", amount)
        );
        this.settlementTitle = settlementTitle;
        this.groupId = groupId;
        this.creatorName = creatorName;
        this.amount = amount;
    }
}
