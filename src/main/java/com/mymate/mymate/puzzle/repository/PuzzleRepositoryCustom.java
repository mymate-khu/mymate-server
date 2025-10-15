package com.mymate.mymate.puzzle.repository;

import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PuzzleRepositoryCustom {

    // 복합 조건 검색
    Page<Puzzle> findByConditions(Long memberId, PuzzleStatus status, String category, 
                                 LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 멤버가 속한 그룹의 모든 멤버들의 복합 조건 검색
    Page<Puzzle> findByConditionsForGroupMembers(List<Long> memberIds, PuzzleStatus status, String category, 
                                                 LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 텍스트 검색 (제목, 설명)
    Page<Puzzle> searchByText(Long memberId, String searchText, Pageable pageable);

    // 멤버가 속한 그룹의 모든 멤버들의 텍스트 검색
    Page<Puzzle> searchByTextForGroupMembers(List<Long> memberIds, String searchText, Pageable pageable);

    // 우선순위별 퍼즐 조회
    Page<Puzzle> findByPriority(Long memberId, String priority, Pageable pageable);

    // 반복 퍼즐 생성 (배치용)
    List<Puzzle> findPuzzlesToGenerateRecurrence(LocalDate targetDate);

    // 통계용 쿼리들
    long countByMemberIdAndStatus(Long memberId, PuzzleStatus status);
    
    long countByMemberIdAndDateRange(Long memberId, LocalDate startDate, LocalDate endDate);
    
    long countCompletedByMemberIdAndDateRange(Long memberId, LocalDate startDate, LocalDate endDate);
}
