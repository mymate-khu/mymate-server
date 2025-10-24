package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 퍼즐 마감 임박 이벤트
 */
@Getter
public class PuzzleDueSoonEvent extends NotificationEvent {
    
    private final String puzzleTitle;
    private final Long groupId;
    
    public PuzzleDueSoonEvent(Long groupId, String puzzleTitle) {
        super(
            null, // 그룹 멤버들에게 발송
            null,
            NotificationType.PUZZLE_DUE_SOON,
            "퍼즐 마감이 임박했습니다",
            "'" + puzzleTitle + "' 퍼즐의 마감이 임박했습니다.",
            Priority.HIGH,
            "/puzzles/" + groupId,
            Map.of("puzzleTitle", puzzleTitle, "groupId", groupId)
        );
        this.puzzleTitle = puzzleTitle;
        this.groupId = groupId;
    }
}
