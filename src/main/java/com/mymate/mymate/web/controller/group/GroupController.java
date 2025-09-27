package com.mymate.mymate.web.controller.group;

import com.mymate.mymate.group.dto.GroupCreateRequest;
import com.mymate.mymate.group.dto.GroupResponse;
import com.mymate.mymate.group.dto.GroupUpdateNameRequest;
import com.mymate.mymate.group.service.GroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Group", description = "그룹 관리 API")
@SecurityRequirement(name = "accessToken")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @Operation(
        summary = "그룹 생성", 
        description = "새로운 그룹을 생성합니다. 기본적으로 회원가입 시 자동으로 그룹이 생성되지만, 그룹을 탈퇴한 후 다시 새로운 그룹을 만들고 싶을 때 사용하는 API입니다.",
        tags = {"Group"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody GroupCreateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        GroupResponse response = groupService.createGroup(request, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
        summary = "내 그룹 목록 조회", 
        description = "현재 사용자가 속한 그룹 목록을 조회합니다. 사용자가 자신이 참여한 모든 그룹을 확인할 때 사용합니다.",
        tags = {"Group"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND"}
        )
    })
    public ResponseEntity<List<GroupResponse>> getMyGroups(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<GroupResponse> response = groupService.getMyGroups(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/leave")
    @Operation(
        summary = "그룹 탈퇴", 
        description = "현재 사용자가 그룹에서 탈퇴합니다. 사용자가 더 이상 그룹에 참여하고 싶지 않을 때 사용합니다.",
        tags = {"Group"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "MEMBER_NOT_IN_GROUP"}
        )
    })
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable Long groupId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        groupService.leaveGroup(groupId, userPrincipal.getId());
        return ApiResponse.onSuccess(GroupSuccessStatus.GROUP_LEFT);
    }

    @PostMapping("/{groupId}/members")
    @Operation(
        summary = "그룹 멤버 직접 추가", 
        description = "그룹에 멤버를 직접 추가합니다. 그룹장이나 관리자가 특정 사용자를 그룹에 바로 초대할 때 사용합니다.",
        tags = {"Group"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "MEMBER_ALREADY_IN_GROUP", "FORBIDDEN"}
        )
    })
    public ResponseEntity<ApiResponse<Void>> addMember(
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable Long groupId,
            @Parameter(description = "추가할 멤버 ID", required = true)
            @RequestParam Long memberId,
            @Parameter(description = "요청자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        groupService.addMember(groupId, memberId, userPrincipal.getId());
        return ApiResponse.onSuccess(GroupSuccessStatus.GROUP_MEMBER_ADDED);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    @Operation(
        summary = "그룹 멤버 제거", 
        description = "그룹에서 멤버를 제거합니다. 그룹장이나 관리자가 특정 멤버를 그룹에서 강제로 제외할 때 사용합니다.",
        tags = {"Group"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "MEMBER_NOT_IN_GROUP", "FORBIDDEN", "CANNOT_REMOVE_OWNER"}
        )
    })
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable Long groupId,
            @Parameter(description = "제거할 멤버 ID", required = true)
            @PathVariable Long memberId,
            @Parameter(description = "요청자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        groupService.removeMember(groupId, memberId, userPrincipal.getId());
        return ApiResponse.onSuccess(GroupSuccessStatus.GROUP_MEMBER_REMOVED);
    }

    @PutMapping("/{groupId}/name")
    @Operation(
        summary = "그룹 이름 변경", 
        description = "그룹의 이름을 변경합니다. 그룹장만 그룹 이름을 변경할 수 있습니다.",
        tags = {"Group"}
    )
    @ApiErrorCodeExamples({
        @ApiErrorCodeExample(
            value = GroupErrorStatus.class,
            codes = {"GROUP_NOT_FOUND", "FORBIDDEN"}
        )
    })
    public ResponseEntity<GroupResponse> updateGroupName(
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable Long groupId,
            @Valid @RequestBody GroupUpdateNameRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        GroupResponse response = groupService.updateGroupName(groupId, request, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }
}
