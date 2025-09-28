package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.Priority;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.enums.RecurrenceType;
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
public class PuzzleResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate scheduledDate;
    private LocalDateTime completedAt;
    private PuzzleStatus status;
    private Long memberId;
    private RecurrenceType recurrenceType;
    private LocalDate recurrenceEndDate;
    private Long parentPuzzleId;
    private Priority priority;
    private String category;
    private String color;
    private LocalDateTime createdAt;
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
                .color(puzzle.getColor())
                .createdAt(puzzle.getCreatedAt())
                .updatedAt(puzzle.getUpdatedAt())
                .build();
    }
}
