package com.mymate.mymate.puzzle.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import com.mymate.mymate.puzzle.enums.Priority;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.enums.RecurrenceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "puzzle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Puzzle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PuzzleStatus status = PuzzleStatus.INPROGRESS;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurrenceType recurrenceType = RecurrenceType.NONE;

    @Column
    private LocalDate recurrenceEndDate;

    @Column
    private Long parentPuzzleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @Column(length = 50)
    private String category;

    @Column(length = 7)
    private String color;

    @Builder
    public Puzzle(String title, String description, LocalDate scheduledDate, Long memberId,
                  RecurrenceType recurrenceType, LocalDate recurrenceEndDate, Long parentPuzzleId,
                  Priority priority, String category, String color) {
        this.title = title;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.memberId = memberId;
        this.recurrenceType = recurrenceType;
        this.recurrenceEndDate = recurrenceEndDate;
        this.parentPuzzleId = parentPuzzleId;
        this.priority = priority;
        this.category = category;
        this.color = color;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void updatePriority(Priority priority) {
        this.priority = priority;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void complete() {
        this.status = PuzzleStatus.DONE;
        this.completedAt = LocalDateTime.now();
    }

    public void incomplete() {
        this.status = PuzzleStatus.INPROGRESS;
        this.completedAt = null;
    }
}
