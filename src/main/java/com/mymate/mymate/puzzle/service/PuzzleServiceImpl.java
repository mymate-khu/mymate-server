package com.mymate.mymate.puzzle.service;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.puzzle.PuzzleHandler;
import com.mymate.mymate.puzzle.dto.PuzzleCreateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleListResponse;
import com.mymate.mymate.puzzle.dto.PuzzleResponse;
import com.mymate.mymate.puzzle.dto.PuzzleStatusUpdateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleUpdateRequest;
import com.mymate.mymate.puzzle.entity.Puzzle;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.repository.PuzzleRepository;
import com.mymate.mymate.puzzle.repository.PuzzleRepositoryCustom;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.common.exception.puzzle.status.PuzzleErrorStatus;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;
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
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public PuzzleResponse createPuzzle(Long memberId, PuzzleCreateRequest request) {
        log.info("[PuzzleService] create start: memberId={}, title={}, scheduledDate={}",
                memberId, request.getTitle(), request.getScheduledDate());
        // 반복 설정 검증
        validateRecurrenceSetting(request.getRecurrenceType(), request.getRecurrenceEndDate(), request.getScheduledDate());

        Long groupId = getGroupId(memberId);

        Puzzle puzzle = Puzzle.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledDate(request.getScheduledDate())
                .memberId(memberId)
                .groupId(groupId)
                .recurrenceType(request.getRecurrenceType())
                .recurrenceEndDate(request.getRecurrenceEndDate())
                .priority(request.getPriority())
                .category(request.getCategory())
                .build();

        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        log.info("[PuzzleService] create done: puzzleId={}, memberId={}, groupId={}",
                savedPuzzle.getId(), memberId, savedPuzzle.getGroupId());
        
        String memberLoginIdForCreate = getMemberLoginId(memberId);
        return PuzzleResponse.from(savedPuzzle, memberLoginIdForCreate);
    }

    @Override
    public PuzzleResponse getPuzzle(Long memberId, Long puzzleId) {
        log.info("[PuzzleService] get start: memberId={}, puzzleId={}", memberId, puzzleId);
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new PuzzleHandler(PuzzleErrorStatus.PUZZLE_NOT_FOUND));
        log.info("[PuzzleService] get done: puzzleId={}, memberId={}, groupId={}",
                puzzleId, memberId, puzzle.getGroupId());
        
        String memberLoginIdForGet = getMemberLoginId(memberId);
        return PuzzleResponse.from(puzzle, memberLoginIdForGet);
    }

    @Override
    public PuzzleListResponse getPuzzles(Long memberId, Pageable pageable) {
        Long groupId = getGroupId(memberId);
        if (groupId == null) {
            throw new PuzzleHandler(PuzzleErrorStatus.GROUP_NOT_FOUND);
        }
        log.info("[PuzzleService] list start: memberId={}, groupId={}, pageable={}", memberId, groupId, pageable);
        Page<Puzzle> puzzlePage = (groupId != null)
                ? puzzleRepository.findByGroupId(groupId, pageable)
                : puzzleRepository.findByMemberId(memberId, pageable);
        
        // 퍼즐 작성자들의 memberLoginId 조회
        java.util.Map<Long, String> memberLoginIdMap = getMemberLoginIdMap(puzzlePage.getContent());
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast(),
                memberLoginIdMap
        );
    }

    @Override
    @Transactional
    public PuzzleResponse updatePuzzle(Long memberId, Long puzzleId, PuzzleUpdateRequest request) {
        log.info("[PuzzleService] update start: memberId={}, puzzleId={}", memberId, puzzleId);
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new PuzzleHandler(PuzzleErrorStatus.PUZZLE_NOT_FOUND));

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
        log.info("[PuzzleService] update done: puzzleId={}, memberId={}", puzzleId, memberId);
        
        String memberLoginIdForUpdate = getMemberLoginId(memberId);
        return PuzzleResponse.from(savedPuzzle, memberLoginIdForUpdate);
    }

    @Override
    @Transactional
    public void deletePuzzle(Long memberId, Long puzzleId) {
        log.info("[PuzzleService] delete start: memberId={}, puzzleId={}", memberId, puzzleId);
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new PuzzleHandler(PuzzleErrorStatus.PUZZLE_NOT_FOUND));

        puzzleRepository.delete(puzzle);
        log.info("[PuzzleService] delete done: puzzleId={}, memberId={}", puzzleId, memberId);
    }

    @Override
    @Transactional
    public PuzzleResponse updatePuzzleStatus(Long memberId, Long puzzleId, PuzzleStatusUpdateRequest request) {
        log.info("[PuzzleService] status update start: memberId={}, puzzleId={}, targetStatus={}",
                memberId, puzzleId, request.getStatus());
        Puzzle puzzle = puzzleRepository.findByIdAndMemberId(puzzleId, memberId)
                .orElseThrow(() -> new PuzzleHandler(PuzzleErrorStatus.PUZZLE_NOT_FOUND));

        if (request.getStatus() == PuzzleStatus.DONE) {
            if (puzzle.getStatus() == PuzzleStatus.DONE) {
                throw new PuzzleHandler(PuzzleErrorStatus.PUZZLE_ALREADY_COMPLETED);
            }
            puzzle.complete();
        } else if (request.getStatus() == PuzzleStatus.INPROGRESS) {
            if (puzzle.getStatus() == PuzzleStatus.INPROGRESS) {
                throw new PuzzleHandler(PuzzleErrorStatus.PUZZLE_NOT_COMPLETED);
            }
            puzzle.incomplete();
        }

        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        log.info("[PuzzleService] status update done: puzzleId={}, memberId={}, status={}",
                puzzleId, memberId, request.getStatus());
        
        String memberLoginIdForStatus = getMemberLoginId(memberId);
        return PuzzleResponse.from(savedPuzzle, memberLoginIdForStatus);
    }

    @Override
    public List<PuzzleResponse> getPuzzlesByDate(Long memberId, LocalDate date) {
        Long groupId = getGroupId(memberId);
        if (groupId == null) {
            throw new PuzzleHandler(PuzzleErrorStatus.GROUP_NOT_FOUND);
        }
        log.info("[PuzzleService] list by date start: memberId={}, groupId={}, date={}", memberId, groupId, date);
        List<Puzzle> puzzles = (groupId != null)
                ? puzzleRepository.findByGroupIdAndScheduledDate(groupId, date)
                : puzzleRepository.findByMemberIdAndScheduledDate(memberId, date);
        return puzzles.stream()
                .map(p -> PuzzleResponse.from(p, getMemberLoginId(memberId)))
                .toList();
    }

    @Override
    public List<PuzzleResponse> getPuzzlesByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        Long groupId = getGroupId(memberId);
        if (groupId == null) {
            throw new PuzzleHandler(PuzzleErrorStatus.GROUP_NOT_FOUND);
        }
        log.info("[PuzzleService] list by range start: memberId={}, groupId={}, startDate={}, endDate={}",
                memberId, groupId, startDate, endDate);
        List<Puzzle> puzzles = (groupId != null)
                ? puzzleRepository.findByGroupIdAndScheduledDateBetween(groupId, startDate, endDate)
                : puzzleRepository.findByMemberIdAndScheduledDateBetween(memberId, startDate, endDate);
        return puzzles.stream()
                .map(p -> PuzzleResponse.from(p, getMemberLoginId(memberId)))
                .toList();
    }

    @Override
    public PuzzleListResponse getPuzzlesByStatus(Long memberId, PuzzleStatus status, Pageable pageable) {
        Long groupId = getGroupId(memberId);
        if (groupId == null) {
            throw new PuzzleHandler(PuzzleErrorStatus.GROUP_NOT_FOUND);
        }
        log.info("[PuzzleService] list by status start: memberId={}, groupId={}, status={}, pageable={}",
                memberId, groupId, status, pageable);
        Page<Puzzle> puzzlePage = (groupId != null)
                ? puzzleRepository.findByGroupIdAndStatus(groupId, status, pageable)
                : puzzleRepository.findByMemberIdAndStatus(memberId, status, pageable);
        
        // 퍼즐 작성자들의 memberLoginId 조회
        java.util.Map<Long, String> memberLoginIdMap = getMemberLoginIdMap(puzzlePage.getContent());
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast(),
                memberLoginIdMap
        );
    }

    @Override
    public PuzzleListResponse getPuzzlesByCategory(Long memberId, String category, Pageable pageable) {
        Long groupId = getGroupId(memberId);
        if (groupId == null) {
            throw new PuzzleHandler(PuzzleErrorStatus.GROUP_NOT_FOUND);
        }
        log.info("[PuzzleService] list by category start: memberId={}, groupId={}, category={}, pageable={}",
                memberId, groupId, category, pageable);
        Page<Puzzle> puzzlePage = puzzleRepository.findByMemberIdAndCategory(memberId, category, pageable);
        
        // 퍼즐 작성자들의 memberLoginId 조회
        java.util.Map<Long, String> memberLoginIdMap = getMemberLoginIdMap(puzzlePage.getContent());
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast(),
                memberLoginIdMap
        );
    }

    private Long getGroupId(Long memberId) {
        List<GroupMember> memberships = groupMemberRepository.findByMemberId(memberId);
        Long groupId = memberships.stream().findFirst().map(GroupMember::getGroupId).orElse(null);
        if (groupId == null) {
            log.warn("[PuzzleService] group not found for memberId={}", memberId);
        }
        return groupId;
    }

    private String getMemberLoginId(Long memberId) {
        return memberRepository.findById(memberId)
                .map(Member::getUserId)
                .orElse(null);
    }

    private java.util.Map<Long, String> getMemberLoginIdMap(List<Puzzle> puzzles) {
        java.util.Set<Long> memberIds = puzzles.stream()
                .map(Puzzle::getMemberId)
                .collect(java.util.stream.Collectors.toSet());
        
        return memberRepository.findAllById(memberIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        Member::getId,
                        Member::getUserId
                ));
    }

    @Override
    public PuzzleListResponse searchPuzzles(Long memberId, String searchText, Pageable pageable) {
        Long groupId = getGroupId(memberId);
        if (groupId == null) {
            throw new PuzzleHandler(PuzzleErrorStatus.GROUP_NOT_FOUND);
        }
        log.info("[PuzzleService] search start: memberId={}, groupId={}, q={}, pageable={}", memberId, groupId, searchText, pageable);
        Page<Puzzle> puzzlePage = puzzleRepositoryCustom.searchByText(memberId, searchText, pageable);
        
        // 퍼즐 작성자들의 memberLoginId 조회
        java.util.Map<Long, String> memberLoginIdMap = getMemberLoginIdMap(puzzlePage.getContent());
        
        return PuzzleListResponse.from(
                puzzlePage.getContent(),
                puzzlePage.getTotalElements(),
                puzzlePage.getTotalPages(),
                puzzlePage.getNumber(),
                puzzlePage.getSize(),
                puzzlePage.isFirst(),
                puzzlePage.isLast(),
                memberLoginIdMap
        );
    }

    private void validateRecurrenceSetting(com.mymate.mymate.puzzle.enums.RecurrenceType recurrenceType, 
                                         LocalDate recurrenceEndDate, LocalDate scheduledDate) {
        if (recurrenceType != com.mymate.mymate.puzzle.enums.RecurrenceType.NONE) {
            if (recurrenceEndDate == null) {
                throw new PuzzleHandler(PuzzleErrorStatus.INVALID_RECURRENCE_SETTING);
            }
            if (recurrenceEndDate.isBefore(scheduledDate)) {
                throw new PuzzleHandler(PuzzleErrorStatus.INVALID_RECURRENCE_END_DATE);
            }
        }
    }
}
