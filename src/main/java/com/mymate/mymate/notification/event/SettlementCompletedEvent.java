package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 정산 완료 이벤트
 */
@Getter
public class SettlementCompletedEvent extends NotificationEvent {
    
    private final String settlementTitle;
    private final Long groupId;
    private final String completerName;
    private final Double amount;
    
    public SettlementCompletedEvent(Long groupId, Long completerId, String settlementTitle, String completerName, Double amount) {
        super(
            null, // 그룹 멤버들에게 발송
            completerId,
            NotificationType.SETTLEMENT_COMPLETED,
            "정산이 완료되었습니다",
            completerName + "님이 '" + settlementTitle + "' 정산을 완료했습니다. (금액: " + amount + "원)",
            Priority.HIGH,
            "/settlements/" + groupId,
            Map.of("settlementTitle", settlementTitle, "groupId", groupId, "completerName", completerName, "amount", amount)
        );
        this.settlementTitle = settlementTitle;
        this.groupId = groupId;
        this.completerName = completerName;
        this.amount = amount;
    }
}
