package com.mymate.mymate.web.controller.group;

import com.mymate.mymate.group.dto.InvitationCreateRequest;
import com.mymate.mymate.group.dto.InvitationResponse;
import com.mymate.mymate.group.service.InvitationService;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.group.status.GroupSuccessStatus;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiErrorCodeExamples;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.auth.jwt.UserPrincipal;
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
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Invitation", description = "초대 관리 API")
@SecurityRequirement(name = "accessToken")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/groups/{groupId}/invitations")
    @Operation(
        summary = "그룹 초대 생성", 
        description = "특정 그룹에 사용자를 초대합니다. 그룹장이나 관리자가 다른 사용자를 그룹에 초대할 때 사용합니다.",
        tags = {"Invitation"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "INVITATION_ALREADY_EXISTS", "FORBIDDEN", "MEMBER_ALREADY_IN_GROUP"}
        )
    })
    public ResponseEntity<InvitationResponse> createInvitation(
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable Long groupId,
            @Valid @RequestBody InvitationCreateRequest request,
            @Parameter(description = "초대자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        InvitationResponse response = invitationService.createInvitation(request, groupId, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
        summary = "내 초대 목록 조회", 
        description = "현재 사용자에게 온 초대 목록을 조회합니다. 사용자가 자신에게 온 그룹 초대를 확인할 때 사용합니다.",
        tags = {"Invitation"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"INVITATION_NOT_FOUND"}
        )
    })
    public ResponseEntity<List<InvitationResponse>> getMyInvitations(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<InvitationResponse> response = invitationService.getMyInvitations(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{invitationId}/accept")
    @Operation(
        summary = "초대 수락", 
        description = "받은 초대를 수락합니다. 사용자가 그룹 초대를 받아서 그룹에 참여하고 싶을 때 사용합니다.",
        tags = {"Invitation"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"INVITATION_NOT_FOUND", "INVITATION_EXPIRED", "INVITATION_ALREADY_PROCESSED"}
        )
    })
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @Parameter(description = "초대 ID", required = true)
            @PathVariable Long invitationId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        invitationService.acceptInvitation(invitationId, userPrincipal.getId());
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_ACCEPTED);
    }

    @PostMapping("/{invitationId}/cancel")
    @Operation(
        summary = "초대 취소", 
        description = "초대를 취소합니다. 초대자가 보낸 초대를 취소하거나, 초대받은 사용자가 초대를 거절할 때 사용합니다.",
        tags = {"Invitation"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"INVITATION_NOT_FOUND", "INVITATION_ALREADY_PROCESSED", "FORBIDDEN"}
        )
    })
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(
            @Parameter(description = "초대 ID", required = true)
            @PathVariable Long invitationId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        invitationService.cancelInvitation(invitationId, userPrincipal.getId());
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_CANCELLED);
    }
}