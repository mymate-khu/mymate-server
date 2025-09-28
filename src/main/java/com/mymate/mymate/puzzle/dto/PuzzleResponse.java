package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.Priority;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.enums.RecurrenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "퍼즐 응답", example = """
{
  "id": 1,
  "title": "운동하기",
  "description": "매일 30분 운동하기",
  "scheduledDate": "2024-01-15",
  "completedAt": null,
  "status": "PENDING",
  "memberId": 123,
  "recurrenceType": "DAILY",
  "recurrenceEndDate": "2024-01-31",
  "parentPuzzleId": null,
  "priority": "HIGH",
  "category": "건강",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
""")
public class PuzzleResponse {

    @Schema(description = "퍼즐 ID", example = "1")
    private Long id;
    
    @Schema(description = "제목", example = "운동하기")
    private String title;
    
    @Schema(description = "설명", example = "매일 30분 운동하기")
    private String description;
    
    @Schema(description = "예정일", example = "2024-01-15")
    private LocalDate scheduledDate;
    
    @Schema(description = "완료일시", example = "null")
    private LocalDateTime completedAt;
    
    @Schema(description = "상태", example = "PENDING")
    private PuzzleStatus status;
    
    @Schema(description = "멤버 ID", example = "123")
    private Long memberId;
    
    @Schema(description = "반복 타입", example = "DAILY")
    private RecurrenceType recurrenceType;
    
    @Schema(description = "반복 종료일", example = "2024-01-31")
    private LocalDate recurrenceEndDate;
    
    @Schema(description = "부모 퍼즐 ID", example = "null")
    private Long parentPuzzleId;
    
    @Schema(description = "우선순위", example = "HIGH")
    private Priority priority;
    
    @Schema(description = "카테고리", example = "건강")
    private String category;
    
    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    public static PuzzleResponse from(Puzzle puzzle) {
        return PuzzleResponse.builder()
                .id(puzzle.getId())
                .title(puzzle.getTitle())
                .description(puzzle.getDescription())
                .scheduledDate(puzzle.getScheduledDate())
                .completedAt(puzzle.getCompletedAt())
                .status(puzzle.getStatus())
                .memberId(puzzle.getMemberId())
                .recurrenceType(puzzle.getRecurrenceType())
                .recurrenceEndDate(puzzle.getRecurrenceEndDate())
                .parentPuzzleId(puzzle.getParentPuzzleId())
                .priority(puzzle.getPriority())
                .category(puzzle.getCategory())
                .createdAt(puzzle.getCreatedAt())
                .updatedAt(puzzle.getUpdatedAt())
                .build();
    }
}
