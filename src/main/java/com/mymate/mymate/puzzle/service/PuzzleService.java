package com.mymate.mymate.puzzle.service;

import com.mymate.mymate.puzzle.dto.PuzzleCreateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleListResponse;
import com.mymate.mymate.puzzle.dto.PuzzleResponse;
import com.mymate.mymate.puzzle.dto.PuzzleStatusUpdateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleUpdateRequest;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PuzzleService {

    // 퍼즐 생성
    PuzzleResponse createPuzzle(Long memberId, PuzzleCreateRequest request);

    // 퍼즐 상세 조회
    PuzzleResponse getPuzzle(Long memberId, Long puzzleId);

    // 퍼즐 목록 조회 (페이징)
    PuzzleListResponse getPuzzles(Long memberId, Pageable pageable);

    // 퍼즐 수정
    PuzzleResponse updatePuzzle(Long memberId, Long puzzleId, PuzzleUpdateRequest request);

    // 퍼즐 삭제
    void deletePuzzle(Long memberId, Long puzzleId);

    // 퍼즐 상태 변경
    PuzzleResponse updatePuzzleStatus(Long memberId, Long puzzleId, PuzzleStatusUpdateRequest request);

    // 특정 날짜 퍼즐 조회
    List<PuzzleResponse> getPuzzlesByDate(Long memberId, LocalDate date);

    // 날짜 범위 퍼즐 조회
    List<PuzzleResponse> getPuzzlesByDateRange(Long memberId, LocalDate startDate, LocalDate endDate);

    // 상태별 퍼즐 조회
    PuzzleListResponse getPuzzlesByStatus(Long memberId, PuzzleStatus status, Pageable pageable);

    // 카테고리별 퍼즐 조회
    PuzzleListResponse getPuzzlesByCategory(Long memberId, String category, Pageable pageable);

    // 텍스트 검색
    PuzzleListResponse searchPuzzles(Long memberId, String searchText, Pageable pageable);
}
