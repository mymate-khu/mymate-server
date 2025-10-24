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
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;
import com.mymate.mymate.common.exception.member.MemberHandler;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
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
    private final MemberRepository memberRepository;

    @PostMapping("/invitations")
    @Operation(
        summary = "그룹 초대 생성", 
        description = "현재 사용자의 그룹에 여러 사용자를 동시에 초대합니다. 사용자당 하나의 그룹만 가질 수 있으므로 그룹 ID는 필요하지 않습니다. 사용자 ID 또는 이메일 목록으로 초대할 수 있습니다.",
        tags = {"Invitation"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "INVITATION_ALREADY_EXISTS", "FORBIDDEN", "MEMBER_ALREADY_IN_GROUP"}
        )
    })
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> createInvitations(
            @Valid @RequestBody InvitationCreateRequest request,
            @Parameter(description = "초대자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        // UserPrincipal의 id로 Member를 조회하여 memberLoginId를 가져옴
        Member inviter = memberRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        List<InvitationResponse> responses = invitationService.createInvitations(request, inviter.getUserId());
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_CREATED, responses);
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
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyInvitations(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<InvitationResponse> response = invitationService.getMyInvitations(userPrincipal.getId());
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_LIST_FETCHED, response);
    }

    @GetMapping("/sent")
    @Operation(
        summary = "보낸 초대 목록 조회", 
        description = "현재 사용자가 보낸 초대 목록을 조회합니다. 초대 상태별 필터링이 가능합니다.",
        tags = {"Invitation"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"INVITATION_NOT_FOUND"}
        )
    })
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getSentInvitations(
            @Parameter(description = "초대 상태 필터 (PENDING, ACCEPTED, EXPIRED, CANCELED)", required = false)
            @RequestParam(required = false) String status,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<InvitationResponse> response;
        if (status != null && !status.trim().isEmpty()) {
            response = invitationService.getSentInvitations(userPrincipal.getId(), status);
        } else {
            response = invitationService.getSentInvitations(userPrincipal.getId());
        }
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_LIST_FETCHED, response);
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