package com.mymate.mymate.web.controller.puzzle;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiErrorCodeExamples;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.puzzle.dto.PuzzleCreateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleListResponse;
import com.mymate.mymate.puzzle.dto.PuzzleResponse;
import com.mymate.mymate.puzzle.dto.PuzzleStatusUpdateRequest;
import com.mymate.mymate.puzzle.dto.PuzzleUpdateRequest;
import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import com.mymate.mymate.puzzle.service.PuzzleService;
import com.mymate.mymate.common.exception.puzzle.status.PuzzleErrorStatus;
import com.mymate.mymate.common.exception.puzzle.status.PuzzleSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/puzzles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "퍼즐 관리", description = "퍼즐 CRUD 및 상태 관리")
public class PuzzleController {

    private final PuzzleService puzzleService;

    @PostMapping
    @Operation(
            summary = "퍼즐 생성",
            description = "인증 사용자(멤버)의 소속 그룹 기준으로 퍼즐을 생성합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"INVALID_RECURRENCE_SETTING", "INVALID_RECURRENCE_END_DATE"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleResponse>> createPuzzle(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PuzzleCreateRequest request) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] create requested: memberId={}, title={}", memberId, request.getTitle());
        PuzzleResponse response = puzzleService.createPuzzle(memberId, request);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLE_CREATED, response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "퍼즐 상세 조회",
            description = "인증 사용자 소속 그룹의 퍼즐인지 검증 후 상세 정보를 반환합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"PUZZLE_NOT_FOUND", "FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleResponse>> getPuzzle(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] get requested: memberId={}, id={}", memberId, id);
        PuzzleResponse response = puzzleService.getPuzzle(memberId, id);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLE_FOUND, response);
    }

    @GetMapping
    @Operation(
            summary = "퍼즐 목록 조회",
            description = "인증 사용자가 속한 그룹 기준으로 퍼즐 목록을 페이징 조회합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleListResponse>> getPuzzles(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] list requested: memberId={}, pageable={}", memberId, pageable);
        PuzzleListResponse response = puzzleService.getPuzzles(memberId, pageable);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLES_FOUND, response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "퍼즐 수정",
            description = "인증 사용자 소속 그룹의 퍼즐인지 검증 후 정보를 수정합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"PUZZLE_NOT_FOUND", "FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleResponse>> updatePuzzle(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody PuzzleUpdateRequest request) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] update requested: memberId={}, id={}", memberId, id);
        PuzzleResponse response = puzzleService.updatePuzzle(memberId, id, request);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLE_UPDATED, response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "퍼즐 삭제",
            description = "인증 사용자 소속 그룹의 퍼즐인지 검증 후 삭제합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"PUZZLE_NOT_FOUND", "FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<Void>> deletePuzzle(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] delete requested: memberId={}, id={}", memberId, id);
        puzzleService.deletePuzzle(memberId, id);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLE_DELETED, null);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "퍼즐 상태 변경",
            description = "인증 사용자 소속 그룹의 퍼즐인지 검증 후 진행 상태를 변경합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"PUZZLE_NOT_FOUND", "PUZZLE_ALREADY_COMPLETED", "PUZZLE_NOT_COMPLETED", "FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleResponse>> updatePuzzleStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody PuzzleStatusUpdateRequest request) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] status update requested: memberId={}, id={}, status={}", memberId, id, request.getStatus());
        PuzzleResponse response = puzzleService.updatePuzzleStatus(memberId, id, request);
        
        PuzzleSuccessStatus successStatus = request.getStatus() == PuzzleStatus.DONE 
                ? PuzzleSuccessStatus.PUZZLE_COMPLETED 
                : PuzzleSuccessStatus.PUZZLE_INCOMPLETED;
        
        return ApiResponse.onSuccess(successStatus, response);
    }

    @GetMapping("/date/{date}")
    @Operation(
            summary = "특정 날짜 퍼즐 조회",
            description = "인증 사용자가 속한 그룹 기준으로 특정 날짜의 퍼즐을 조회합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<List<PuzzleResponse>>> getPuzzlesByDate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] list by date requested: memberId={}, date={}", memberId, date);
        List<PuzzleResponse> response = puzzleService.getPuzzlesByDate(memberId, date);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLES_FOUND, response);
    }

    @GetMapping("/date/range")
    @Operation(
            summary = "날짜 범위 퍼즐 조회",
            description = "인증 사용자가 속한 그룹 기준으로 날짜 범위의 퍼즐을 조회합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<List<PuzzleResponse>>> getPuzzlesByDateRange(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] list by range requested: memberId={}, startDate={}, endDate={}", memberId, startDate, endDate);
        List<PuzzleResponse> response = puzzleService.getPuzzlesByDateRange(memberId, startDate, endDate);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLES_FOUND, response);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "상태별 퍼즐 조회",
            description = "인증 사용자가 속한 그룹 기준으로 상태별 퍼즐을 조회합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleListResponse>> getPuzzlesByStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable PuzzleStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] list by status requested: memberId={}, status={}, pageable={}", memberId, status, pageable);
        PuzzleListResponse response = puzzleService.getPuzzlesByStatus(memberId, status, pageable);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLES_FOUND, response);
    }

    @GetMapping("/category/{category}")
    @Operation(
            summary = "카테고리별 퍼즐 조회",
            description = "인증 사용자가 속한 그룹 기준으로 카테고리별 퍼즐을 조회합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleListResponse>> getPuzzlesByCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String category,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] list by category requested: memberId={}, category={}, pageable={}", memberId, category, pageable);
        PuzzleListResponse response = puzzleService.getPuzzlesByCategory(memberId, category, pageable);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLES_FOUND, response);
    }

    @GetMapping("/search")
    @Operation(
            summary = "퍼즐 검색",
            description = "인증 사용자가 속한 그룹 기준으로 제목/설명 텍스트를 검색합니다."
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PuzzleErrorStatus.class,
                    codes = {"FORBIDDEN"}
            )
    })
    public ResponseEntity<ApiResponse<PuzzleListResponse>> searchPuzzles(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String q,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Long memberId = principal.getId();
        log.info("[Puzzle] search requested: memberId={}, q={}, pageable={}", memberId, q, pageable);
        PuzzleListResponse response = puzzleService.searchPuzzles(memberId, q, pageable);
        return ApiResponse.onSuccess(PuzzleSuccessStatus.PUZZLES_FOUND, response);
    }
}
