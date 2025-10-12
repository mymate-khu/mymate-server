package com.mymate.mymate.web.controller.mateboard;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiErrorCodeExamples;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.mateboard.status.MateBoardErrorStatus;
import com.mymate.mymate.common.exception.mateboard.status.MateBoardSuccessStatus;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.mateboard.dto.MateBoardCreateRequest;
import com.mymate.mymate.mateboard.dto.MateBoardListResponse;
import com.mymate.mymate.mateboard.dto.MateBoardResponse;
import com.mymate.mymate.mateboard.dto.MateBoardUpdateRequest;
import com.mymate.mymate.mateboard.service.MateBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mateboards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MateBoard", description = "메이트보드 관리 API")
@SecurityRequirement(name = "accessToken")
public class MateBoardController {

    private final MateBoardService mateBoardService;

    @PostMapping
    @Operation(
            summary = "메이트보드 생성",
            description = "새로운 메이트보드를 생성합니다. 그룹 내 모든 멤버가 볼 수 있으며, 24시간 후 자동으로 삭제됩니다.",
            tags = {"MateBoard"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            ),
            @ApiErrorCodeExample(
                    value = MateBoardErrorStatus.class,
                    codes = {"INVALID_CONTENT"}
            )
    })
    public ResponseEntity<ApiResponse<MateBoardResponse>> createMateBoard(
            @Valid @RequestBody MateBoardCreateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        MateBoardResponse response = mateBoardService.createMateBoard(userPrincipal.getId(), request);
        return ApiResponse.onSuccess(MateBoardSuccessStatus.MATEBOARD_CREATED, response);
    }

    @GetMapping("/{mateBoardId}")
    @Operation(
            summary = "메이트보드 상세 조회",
            description = "특정 메이트보드의 상세 정보를 조회합니다. 같은 그룹의 멤버만 조회할 수 있습니다.",
            tags = {"MateBoard"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = MateBoardErrorStatus.class,
                    codes = {"MATEBOARD_NOT_FOUND", "MATEBOARD_EXPIRED", "MATEBOARD_ACCESS_DENIED"}
            ),
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<MateBoardResponse>> getMateBoard(
            @Parameter(description = "메이트보드 ID", required = true)
            @PathVariable Long mateBoardId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        MateBoardResponse response = mateBoardService.getMateBoard(userPrincipal.getId(), mateBoardId);
        return ApiResponse.onSuccess(MateBoardSuccessStatus.MATEBOARD_FOUND, response);
    }

    @GetMapping
    @Operation(
            summary = "메이트보드 목록 조회",
            description = "현재 사용자가 속한 그룹의 메이트보드 목록을 페이징하여 조회합니다. 최신순으로 정렬되며, 만료되지 않은 메이트보드만 조회됩니다.",
            tags = {"MateBoard"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<MateBoardListResponse>> getMateBoards(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        MateBoardListResponse response = mateBoardService.getMateBoards(userPrincipal.getId(), pageable);
        return ApiResponse.onSuccess(MateBoardSuccessStatus.MATEBOARD_LIST_FOUND, response);
    }

    @PutMapping("/{mateBoardId}")
    @Operation(
            summary = "메이트보드 수정",
            description = "메이트보드의 내용을 수정합니다. 작성자만 수정할 수 있으며, 만료된 메이트보드는 수정할 수 없습니다.",
            tags = {"MateBoard"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = MateBoardErrorStatus.class,
                    codes = {"MATEBOARD_NOT_FOUND", "MATEBOARD_EXPIRED", "MATEBOARD_UPDATE_DENIED", "INVALID_CONTENT"}
            )
    })
    public ResponseEntity<ApiResponse<MateBoardResponse>> updateMateBoard(
            @Parameter(description = "메이트보드 ID", required = true)
            @PathVariable Long mateBoardId,
            @Valid @RequestBody MateBoardUpdateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        MateBoardResponse response = mateBoardService.updateMateBoard(userPrincipal.getId(), mateBoardId, request);
        return ApiResponse.onSuccess(MateBoardSuccessStatus.MATEBOARD_UPDATED, response);
    }

    @DeleteMapping("/{mateBoardId}")
    @Operation(
            summary = "메이트보드 삭제",
            description = "메이트보드를 삭제합니다. 작성자만 삭제할 수 있습니다.",
            tags = {"MateBoard"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = MateBoardErrorStatus.class,
                    codes = {"MATEBOARD_NOT_FOUND", "MATEBOARD_DELETE_DENIED"}
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteMateBoard(
            @Parameter(description = "메이트보드 ID", required = true)
            @PathVariable Long mateBoardId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        mateBoardService.deleteMateBoard(userPrincipal.getId(), mateBoardId);
        return ApiResponse.onSuccess(MateBoardSuccessStatus.MATEBOARD_DELETED);
    }
}