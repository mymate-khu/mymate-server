package com.mymate.mymate.puzzle.repository;

import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {

    // 멤버별 퍼즐 조회
    Page<Puzzle> findByMemberId(Long memberId, Pageable pageable);

    // 멤버별 특정 날짜 퍼즐 조회
    List<Puzzle> findByMemberIdAndScheduledDate(Long memberId, LocalDate scheduledDate);

    // 멤버별 날짜 범위 퍼즐 조회
    List<Puzzle> findByMemberIdAndScheduledDateBetween(Long memberId, LocalDate startDate, LocalDate endDate);


    // 그룹 기준 조회(단일 그룹)
    Page<Puzzle> findByGroupId(Long groupId, Pageable pageable);

    List<Puzzle> findByGroupIdAndScheduledDate(Long groupId, LocalDate scheduledDate);

    List<Puzzle> findByGroupIdAndScheduledDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);

    Page<Puzzle> findByGroupIdAndStatus(Long groupId, PuzzleStatus status, Pageable pageable);

    // 그룹 삭제 시 퍼즐 일괄 삭제
    void deleteByGroupId(Long groupId);

    // 멤버별 상태별 퍼즐 조회
    Page<Puzzle> findByMemberIdAndStatus(Long memberId, PuzzleStatus status, Pageable pageable);


    // 멤버별 카테고리별 퍼즐 조회
    Page<Puzzle> findByMemberIdAndCategory(Long memberId, String category, Pageable pageable);


    // 멤버별 퍼즐 조회 (권한 확인용)
    Optional<Puzzle> findByIdAndMemberId(Long id, Long memberId);

    // 반복 퍼즐의 자식 퍼즐들 조회
    List<Puzzle> findByParentPuzzleId(Long parentPuzzleId);

    // 반복 퍼즐의 자식 퍼즐들 중 특정 날짜 이후 조회
    List<Puzzle> findByParentPuzzleIdAndScheduledDateGreaterThanEqual(Long parentPuzzleId, LocalDate fromDate);

    // 멤버별 오늘 퍼즐 개수 조회
    @Query("SELECT COUNT(p) FROM Puzzle p WHERE p.memberId = :memberId AND p.scheduledDate = :date")
    long countByMemberIdAndDate(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    // 멤버별 완료율 조회 (필요 시 QueryDSL 구현 사용)
}
