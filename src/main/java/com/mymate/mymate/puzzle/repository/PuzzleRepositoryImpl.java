package com.mymate.mymate.puzzle.repository;

import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.Priority;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.enums.RecurrenceType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.mymate.mymate.puzzle.entity.QPuzzle.puzzle;

@Repository
public class PuzzleRepositoryImpl implements PuzzleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PuzzleRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Puzzle> findByConditions(Long memberId, PuzzleStatus status, String category,
                                        LocalDate startDate, LocalDate endDate, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(puzzle.memberId.eq(memberId));

        if (status != null) {
            where.and(puzzle.status.eq(status));
        }
        if (category != null && !category.trim().isEmpty()) {
            where.and(puzzle.category.eq(category));
        }
        if (startDate != null) {
            where.and(puzzle.scheduledDate.goe(startDate));
        }
        if (endDate != null) {
            where.and(puzzle.scheduledDate.loe(endDate));
        }

        List<Puzzle> content = queryFactory
                .selectFrom(puzzle)
                .where(where)
                .orderBy(puzzle.scheduledDate.desc(), puzzle.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Puzzle> findByConditionsForGroupMembers(List<Long> memberIds, PuzzleStatus status, String category,
                                                        LocalDate startDate, LocalDate endDate, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(puzzle.memberId.in(memberIds));

        if (status != null) {
            where.and(puzzle.status.eq(status));
        }
        if (category != null && !category.trim().isEmpty()) {
            where.and(puzzle.category.eq(category));
        }
        if (startDate != null) {
            where.and(puzzle.scheduledDate.goe(startDate));
        }
        if (endDate != null) {
            where.and(puzzle.scheduledDate.loe(endDate));
        }

        List<Puzzle> content = queryFactory
                .selectFrom(puzzle)
                .where(where)
                .orderBy(puzzle.scheduledDate.desc(), puzzle.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Puzzle> searchByText(Long memberId, String searchText, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(puzzle.memberId.eq(memberId));

        if (searchText != null && !searchText.isBlank()) {
            String like = "%" + searchText.toLowerCase() + "%";
            where.and(puzzle.title.lower().like(like)
                    .or(puzzle.description.lower().like(like)));
        }

        List<Puzzle> content = queryFactory
                .selectFrom(puzzle)
                .where(where)
                .orderBy(puzzle.scheduledDate.desc(), puzzle.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Puzzle> searchByTextForGroupMembers(List<Long> memberIds, String searchText, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(puzzle.memberId.in(memberIds));

        if (searchText != null && !searchText.isBlank()) {
            String like = "%" + searchText.toLowerCase() + "%";
            where.and(puzzle.title.lower().like(like)
                    .or(puzzle.description.lower().like(like)));
        }

        List<Puzzle> content = queryFactory
                .selectFrom(puzzle)
                .where(where)
                .orderBy(puzzle.scheduledDate.desc(), puzzle.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Puzzle> findByPriority(Long memberId, String priority, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(puzzle.memberId.eq(memberId));

        if (priority != null && !priority.isBlank()) {
            try {
                where.and(puzzle.priority.eq(Priority.valueOf(priority)));
            } catch (IllegalArgumentException ignored) {
                where.and(puzzle.id.isNull());
            }
        }

        List<Puzzle> content = queryFactory
                .selectFrom(puzzle)
                .where(where)
                .orderBy(puzzle.scheduledDate.desc(), puzzle.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public List<Puzzle> findPuzzlesToGenerateRecurrence(LocalDate targetDate) {
        return queryFactory
                .selectFrom(puzzle)
                .where(puzzle.recurrenceType.ne(RecurrenceType.NONE)
                        .and(puzzle.recurrenceEndDate.goe(targetDate))
                        .and(puzzle.parentPuzzleId.isNull()))
                .fetch();
    }

    @Override
    public long countByMemberIdAndStatus(Long memberId, PuzzleStatus status) {
        Long count = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(puzzle.memberId.eq(memberId)
                        .and(puzzle.status.eq(status)))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public long countByMemberIdAndDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        Long count = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(puzzle.memberId.eq(memberId)
                        .and(puzzle.scheduledDate.between(startDate, endDate)))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public long countCompletedByMemberIdAndDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        Long count = queryFactory
                .select(puzzle.count())
                .from(puzzle)
                .where(puzzle.memberId.eq(memberId)
                        .and(puzzle.scheduledDate.between(startDate, endDate))
                        .and(puzzle.status.eq(PuzzleStatus.DONE)))
                .fetchOne();
        return count == null ? 0 : count;
    }
}
