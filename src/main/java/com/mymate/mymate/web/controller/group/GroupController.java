package com.mymate.mymate.web.controller.group;

import com.mymate.mymate.group.dto.GroupCreateRequest;
import com.mymate.mymate.group.dto.GroupResponse;
import com.mymate.mymate.group.service.GroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Group", description = "그룹 관리 API")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다.")
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal Long memberId) {
        
        GroupResponse response = groupService.createGroup(request, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 그룹 목록 조회", description = "현재 사용자가 속한 그룹 목록을 조회합니다.")
    public ResponseEntity<List<GroupResponse>> getMyGroups(
            @AuthenticationPrincipal Long memberId) {
        
        List<GroupResponse> response = groupService.getMyGroups(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/leave")
    @Operation(summary = "그룹 탈퇴", description = "현재 사용자가 그룹에서 탈퇴합니다.")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal Long memberId) {
        
        groupService.leaveGroup(groupId, memberId);
        return ApiResponse.onSuccess(GroupSuccessStatus.GROUP_LEFT);
    }

    @PostMapping("/{groupId}/members")
    @Operation(summary = "그룹 멤버 직접 추가", description = "그룹에 멤버를 직접 추가합니다.")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable Long groupId,
            @RequestParam Long memberId,
            @AuthenticationPrincipal Long requesterId) {
        
        groupService.addMember(groupId, memberId, requesterId);
        return ApiResponse.onSuccess(GroupSuccessStatus.GROUP_MEMBER_ADDED);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    @Operation(summary = "그룹 멤버 제거", description = "그룹에서 멤버를 제거합니다.")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal Long requesterId) {
        
        groupService.removeMember(groupId, memberId, requesterId);
        return ApiResponse.onSuccess(GroupSuccessStatus.GROUP_MEMBER_REMOVED);
    }
}
