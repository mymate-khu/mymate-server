package com.mymate.mymate.web.controller.rulebook;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiErrorCodeExamples;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.rulebook.status.RulebookErrorStatus;
import com.mymate.mymate.common.exception.rulebook.status.RulebookSuccessStatus;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.rulebook.dto.RulebookCreateRequest;
import com.mymate.mymate.rulebook.dto.RulebookResponse;
import com.mymate.mymate.rulebook.dto.RulebookUpdateRequest;
import com.mymate.mymate.rulebook.service.RulebookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rulebooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rulebook", description = "룰북 관리 API")
@SecurityRequirement(name = "accessToken")
public class RulebookController {

    private final RulebookService rulebookService;

    @PostMapping
    @Operation(
            summary = "룰북 생성",
            description = "새로운 룰북을 생성합니다. 그룹 내 모든 멤버가 생성할 수 있습니다.",
            tags = {"Rulebook"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<RulebookResponse>> createRulebook(
            @Valid @RequestBody RulebookCreateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        RulebookResponse response = rulebookService.createRulebook(userPrincipal.getId(), request);
        return ApiResponse.onSuccess(RulebookSuccessStatus.RULEBOOK_CREATED, response);
    }

    @GetMapping("/{rulebookId}")
    @Operation(
            summary = "룰북 상세 조회",
            description = "특정 룰북의 상세 정보를 조회합니다. 같은 그룹의 멤버만 조회할 수 있습니다.",
            tags = {"Rulebook"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = RulebookErrorStatus.class,
                    codes = {"RULEBOOK_NOT_FOUND", "RULEBOOK_ACCESS_DENIED"}
            ),
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<RulebookResponse>> getRulebook(
            @Parameter(description = "룰북 ID", required = true)
            @PathVariable Long rulebookId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        RulebookResponse response = rulebookService.getRulebook(userPrincipal.getId(), rulebookId);
        return ApiResponse.onSuccess(RulebookSuccessStatus.RULEBOOK_FOUND, response);
    }

    @GetMapping
    @Operation(
            summary = "룰북 목록 조회",
            description = "현재 사용자가 속한 그룹의 모든 룰북을 조회합니다. 최신순으로 정렬됩니다.",
            tags = {"Rulebook"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<List<RulebookResponse>>> getRulebooks(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<RulebookResponse> response = rulebookService.getRulebooks(userPrincipal.getId());
        return ApiResponse.onSuccess(RulebookSuccessStatus.RULEBOOK_LIST_FOUND, response);
    }

    @PutMapping("/{rulebookId}")
    @Operation(
            summary = "룰북 수정",
            description = "룰북의 제목이나 내용을 수정합니다. 같은 그룹의 모든 멤버가 수정할 수 있습니다.",
            tags = {"Rulebook"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = RulebookErrorStatus.class,
                    codes = {"RULEBOOK_NOT_FOUND", "RULEBOOK_UPDATE_DENIED"}
            ),
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<RulebookResponse>> updateRulebook(
            @Parameter(description = "룰북 ID", required = true)
            @PathVariable Long rulebookId,
            @Valid @RequestBody RulebookUpdateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        RulebookResponse response = rulebookService.updateRulebook(userPrincipal.getId(), rulebookId, request);
        return ApiResponse.onSuccess(RulebookSuccessStatus.RULEBOOK_UPDATED, response);
    }

    @DeleteMapping("/{rulebookId}")
    @Operation(
            summary = "룰북 삭제",
            description = "룰북을 삭제합니다. 같은 그룹의 모든 멤버가 삭제할 수 있습니다.",
            tags = {"Rulebook"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = RulebookErrorStatus.class,
                    codes = {"RULEBOOK_NOT_FOUND", "RULEBOOK_DELETE_DENIED"}
            ),
            @ApiErrorCodeExample(
                    value = GroupErrorStatus.class,
                    codes = {"GROUP_NOT_FOUND"}
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteRulebook(
            @Parameter(description = "룰북 ID", required = true)
            @PathVariable Long rulebookId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        rulebookService.deleteRulebook(userPrincipal.getId(), rulebookId);
        return ApiResponse.onSuccess(RulebookSuccessStatus.RULEBOOK_DELETED);
    }
}