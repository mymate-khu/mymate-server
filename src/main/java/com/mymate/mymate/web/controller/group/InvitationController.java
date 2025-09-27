package com.mymate.mymate.web.controller.group;

import com.mymate.mymate.group.dto.InvitationCreateRequest;
import com.mymate.mymate.group.dto.InvitationResponse;
import com.mymate.mymate.group.service.InvitationService;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.group.status.GroupSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
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
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/groups/{groupId}/invitations")
    @Operation(summary = "그룹 초대 생성", description = "특정 그룹에 사용자를 초대합니다.")
    public ResponseEntity<InvitationResponse> createInvitation(
            @PathVariable Long groupId,
            @Valid @RequestBody InvitationCreateRequest request,
            @AuthenticationPrincipal Long inviterId) {
        
        InvitationResponse response = invitationService.createInvitation(request, groupId, inviterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 초대 목록 조회", description = "현재 사용자에게 온 초대 목록을 조회합니다.")
    public ResponseEntity<List<InvitationResponse>> getMyInvitations(
            @AuthenticationPrincipal Long memberId) {
        
        List<InvitationResponse> response = invitationService.getMyInvitations(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{invitationId}/accept")
    @Operation(summary = "초대 수락", description = "받은 초대를 수락합니다.")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal Long memberId) {
        
        invitationService.acceptInvitation(invitationId, memberId);
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_ACCEPTED);
    }

    @PostMapping("/{invitationId}/cancel")
    @Operation(summary = "초대 취소", description = "초대를 취소합니다.")
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal Long memberId) {
        
        invitationService.cancelInvitation(invitationId, memberId);
        return ApiResponse.onSuccess(GroupSuccessStatus.INVITATION_CANCELLED);
    }
}
