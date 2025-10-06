package com.mymate.mymate.web.controller.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mymate.mymate.notification.dto.FcmTokenRequest;
import com.mymate.mymate.notification.dto.FcmTokenResponse;
import com.mymate.mymate.notification.service.FcmTokenService;
import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.member.status.MemberSuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications/fcm")
@Tag(name = "FCM 토큰 API", description = "FCM 토큰 등록/조회/삭제")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/token")
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록(업서트)합니다.")
    public ResponseEntity<ApiResponse<FcmTokenResponse>> registerToken(
            @Valid
            @RequestBody
            @Parameter(description = "등록할 FCM 토큰과 디바이스 타입", required = true) FcmTokenRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        FcmTokenResponse body = fcmTokenService.registerToken(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_SAVED, body);
    }

    @GetMapping("/token")
    @Operation(summary = "활성 FCM 토큰 조회", description = "현재 사용자 계정의 활성 토큰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<FcmTokenResponse>> getTokens(@AuthenticationPrincipal UserPrincipal principal) {
        FcmTokenResponse body = fcmTokenService.getActiveTokens(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_FOUND, body);
    }

    @DeleteMapping("/token")
    @Operation(summary = "FCM 토큰 삭제", description = "특정 토큰을 비활성화 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteToken(
            @Parameter(description = "삭제할 FCM 토큰", required = true) @RequestParam String token,
            @AuthenticationPrincipal UserPrincipal principal) {
        fcmTokenService.deleteToken(principal.getId(), token);
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_DELETED, null);
    }
}


