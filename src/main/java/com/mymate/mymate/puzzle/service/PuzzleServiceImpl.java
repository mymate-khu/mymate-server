package com.mymate.mymate.puzzle.service;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.puzzle.dto.PuzzleCreateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleListResponse;
import com.mymate.mymate.puzzle.dto.PuzzleResponse;
import com.mymate.mymate.puzzle.dto.PuzzleStatusUpdateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleUpdateRequest;
import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.repository.PuzzleRepository;
import com.mymate.mymate.puzzle.repository.PuzzleRepositoryCustom;
import com.mymate.mymate.common.exception.puzzle.status.PuzzleErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PuzzleServiceImpl implements PuzzleService {

    private final PuzzleRepository puzzleRepository;
    private final PuzzleRepositoryCustom puzzleRepositoryCustom;

    @Override
    @Transactional
    public PuzzleResponse createPuzzle(Long memberId, PuzzleCreateRequest request) {
        // 반복 설정 검증
        validateRecurrenceSetting(request.getRecurrenceType(), request.getRecurrenceEndDate(), request.getScheduledDate());

        Puzzle puzzle = Puzzle.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledDate(request.getScheduledDate())
                .memberId(memberId)
                .recurrenceType(request.getRecurrenceType())
                .recurrenceEndDate(request.getRecurrenceEndDate())
                .priority(request.getPriority())
                .category(request.getCategory())
                .build();

        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        log.info("퍼즐 생성 완료: puzzleId={}, memberId={}", savedPuzzle.getId(), memberId);
        
        return PuzzleResponse.from(savedPuzzle);
    }

    @Override
    public PuzzleResponse getPuzzle(Long memberId, Long puzzleId) {
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new GeneralException(PuzzleErrorStatus.PUZZLE_NOT_FOUND));
        
        return PuzzleResponse.from(puzzle);
    }

    @Override
    public PuzzleListResponse getPuzzles(Long memberId, Pageable pageable) {
        Page<Puzzle> puzzlePage = puzzleRepository.findByMemberId(memberId, pageable);
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast()
        );
    }

    @Override
    @Transactional
    public PuzzleResponse updatePuzzle(Long memberId, Long puzzleId, PuzzleUpdateRequest request) {
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new GeneralException(PuzzleErrorStatus.PUZZLE_NOT_FOUND));

        // 수정할 필드들 업데이트
        if (request.getTitle() != null) {
            puzzle.updateTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            puzzle.updateDescription(request.getDescription());
        }
        if (request.getScheduledDate() != null) {
            puzzle.updateScheduledDate(request.getScheduledDate());
        }
        if (request.getPriority() != null) {
            puzzle.updatePriority(request.getPriority());
        }
        if (request.getCategory() != null) {
            puzzle.updateCategory(request.getCategory());
        }

        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        log.info("퍼즐 수정 완료: puzzleId={}, memberId={}", puzzleId, memberId);
        
        return PuzzleResponse.from(savedPuzzle);
    }

    @Override
    @Transactional
    public void deletePuzzle(Long memberId, Long puzzleId) {
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new GeneralException(PuzzleErrorStatus.PUZZLE_NOT_FOUND));

        puzzleRepository.delete(puzzle);
        log.info("퍼즐 삭제 완료: puzzleId={}, memberId={}", puzzleId, memberId);
    }

    @Override
    @Transactional
    public PuzzleResponse updatePuzzleStatus(Long memberId, Long puzzleId, PuzzleStatusUpdateRequest request) {
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new GeneralException(PuzzleErrorStatus.PUZZLE_NOT_FOUND));

        if (request.getStatus() == PuzzleStatus.DONE) {
            if (puzzle.getStatus() == PuzzleStatus.DONE) {
                throw new GeneralException(PuzzleErrorStatus.PUZZLE_ALREADY_COMPLETED);
            }
            puzzle.complete();
        } else if (request.getStatus() == PuzzleStatus.INPROGRESS) {
            if (puzzle.getStatus() == PuzzleStatus.INPROGRESS) {
                throw new GeneralException(PuzzleErrorStatus.PUZZLE_NOT_COMPLETED);
            }
            puzzle.incomplete();
        }

        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        log.info("퍼즐 상태 변경 완료: puzzleId={}, memberId={}, status={}", puzzleId, memberId, request.getStatus());
        
        return PuzzleResponse.from(savedPuzzle);
    }

    @Override
    public List<PuzzleResponse> getPuzzlesByDate(Long memberId, LocalDate date) {
        List<Puzzle> puzzles = puzzleRepository.findByMemberIdAndScheduledDate(memberId, date);
        return puzzles.stream()
                .map(PuzzleResponse::from)
                .toList();
    }

    @Override
    public List<PuzzleResponse> getPuzzlesByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        List<Puzzle> puzzles = puzzleRepository.findByMemberIdAndScheduledDateBetween(memberId, startDate, endDate);
        return puzzles.stream()
                .map(PuzzleResponse::from)
                .toList();
    }

    @Override
    public PuzzleListResponse getPuzzlesByStatus(Long memberId, PuzzleStatus status, Pageable pageable) {
        Page<Puzzle> puzzlePage = puzzleRepository.findByMemberIdAndStatus(memberId, status, pageable);
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast()
        );
    }

    @Override
    public PuzzleListResponse getPuzzlesByCategory(Long memberId, String category, Pageable pageable) {
        Page<Puzzle> puzzlePage = puzzleRepository.findByMemberIdAndCategory(memberId, category, pageable);
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast()
        );
    }

    @Override
    public PuzzleListResponse searchPuzzles(Long memberId, String searchText, Pageable pageable) {
        Page<Puzzle> puzzlePage = puzzleRepositoryCustom.searchByText(memberId, searchText, pageable);
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast()
        );
    }

    private void validateRecurrenceSetting(com.mymate.mymate.puzzle.enums.RecurrenceType recurrenceType, 
                                         LocalDate recurrenceEndDate, LocalDate scheduledDate) {
        if (recurrenceType != com.mymate.mymate.puzzle.enums.RecurrenceType.NONE) {
            if (recurrenceEndDate == null) {
                throw new GeneralException(PuzzleErrorStatus.INVALID_RECURRENCE_SETTING);
            }
            if (recurrenceEndDate.isBefore(scheduledDate)) {
                throw new GeneralException(PuzzleErrorStatus.INVALID_RECURRENCE_END_DATE);
            }
        }
    }
}
