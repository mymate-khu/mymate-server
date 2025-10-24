package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 정산 업데이트 이벤트
 */
@Getter
public class SettlementUpdatedEvent extends NotificationEvent {
    
    private final String settlementTitle;
    private final Long groupId;
    private final String updaterName;
    private final Double amount;
    
    public SettlementUpdatedEvent(Long groupId, Long updaterId, String settlementTitle, String updaterName, Double amount) {
        super(
            null, // 그룹 멤버들에게 발송
            updaterId,
            NotificationType.SETTLEMENT_UPDATED,
            "정산이 업데이트되었습니다",
            updaterName + "님이 '" + settlementTitle + "' 정산을 업데이트했습니다. (금액: " + amount + "원)",
            Priority.MEDIUM,
            "/settlements/" + groupId,
            Map.of("settlementTitle", settlementTitle, "groupId", groupId, "updaterName", updaterName, "amount", amount)
        );
        this.settlementTitle = settlementTitle;
        this.groupId = groupId;
        this.updaterName = updaterName;
        this.amount = amount;
    }
}
