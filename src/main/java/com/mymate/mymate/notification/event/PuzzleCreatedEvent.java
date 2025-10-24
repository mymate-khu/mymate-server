package com.mymate.mymate.notification.event;

import java.util.Map;

import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;

import lombok.Getter;

/**
 * 퍼즐 생성 이벤트
 */
@Getter
public class PuzzleCreatedEvent extends NotificationEvent {
    
    private final String puzzleTitle;
    private final Long groupId;
    private final String creatorName;
    
    public PuzzleCreatedEvent(Long groupId, Long creatorId, String puzzleTitle, String creatorName) {
        super(
            null, // 그룹 멤버들에게 발송
            creatorId,
            NotificationType.PUZZLE_CREATED,
            "새로운 퍼즐이 생성되었습니다",
            creatorName + "님이 '" + puzzleTitle + "' 퍼즐을 생성했습니다.",
            Priority.MEDIUM,
            "/puzzles/" + groupId,
            Map.of("puzzleTitle", puzzleTitle, "groupId", groupId, "creatorName", creatorName)
        );
        this.puzzleTitle = puzzleTitle;
        this.groupId = groupId;
        this.creatorName = creatorName;
    }
}
