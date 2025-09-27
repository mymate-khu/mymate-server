package com.mymate.mymate.web.controller.member;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.member.status.MemberSuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mymate.mymate.member.dto.MemberSearchResponse;
import com.mymate.mymate.member.dto.ProfileSummaryResponse;
import com.mymate.mymate.member.dto.ProfileUpdateRequest;
import com.mymate.mymate.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileSummaryResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProfileSummaryResponse response = memberService.getMyProfile(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.MEMBER_INFO_FETCHED, response);
    }

    @Operation(summary = "내 프로필 수정")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileSummaryResponse>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Validated ProfileUpdateRequest request
    ) {
        ProfileSummaryResponse response = memberService.updateMyProfile(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.MEMBER_INFO_FETCHED, response);
    }

    @Operation(summary = "멤버 검색", description = "사용자명 또는 이름으로 회원을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberSearchResponse>>> searchMembers(
            @RequestParam String q
    ) {
        List<MemberSearchResponse> response = memberService.searchMembers(q);
        return ApiResponse.onSuccess(MemberSuccessStatus.MEMBER_INFO_FETCHED, response);
    }
}



