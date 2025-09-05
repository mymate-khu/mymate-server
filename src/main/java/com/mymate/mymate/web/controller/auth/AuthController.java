package com.mymate.mymate.web.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mymate.mymate.auth.dto.LocalLoginRequest;
import com.mymate.mymate.auth.dto.RefreshRequest;
import com.mymate.mymate.auth.dto.SignUpRequest;
import com.mymate.mymate.auth.dto.SocialLoginRequest;
import com.mymate.mymate.auth.dto.TokenResponse;
import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.auth.service.AuthService;
import com.mymate.mymate.auth.service.PhoneVerificationService;
import com.mymate.mymate.auth.token.RefreshTokenStore;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.phone.status.PhoneSuccessStatus;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import com.mymate.mymate.common.exception.member.status.MemberSuccessStatus;
import com.mymate.mymate.common.exception.term.status.TermSuccessStatus;
import com.mymate.mymate.common.exception.token.status.TokenErrorStatus;
import com.mymate.mymate.common.exception.token.status.TokenSuccessStatus;
import com.mymate.mymate.term.dto.AgreementRequest;
import com.mymate.mymate.term.dto.AgreementResponse;
import com.mymate.mymate.term.service.AgreementService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RefreshTokenStore store;
    private final AuthService authService;
    private final AgreementService agreementService;
    private final PhoneVerificationService phoneVerificationService;

    public AuthController(RefreshTokenStore store, AuthService authService, AgreementService agreementService, PhoneVerificationService phoneVerificationService) {
        this.store = store;
        this.authService = authService;
        this.agreementService = agreementService;
        this.phoneVerificationService = phoneVerificationService;
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "리프레시 토큰 회전",
            description = "기존 리프레시 토큰을 검증 후 새 액세스 토큰과 리프레시 토큰을 발급합니다."
    )
    @ApiErrorCodeExample(
            value = TokenErrorStatus.class,
            codes = {"INVALID_REFRESH_TOKEN", "REFRESH_TOKEN_EXPIRED"}
    )
    @ApiErrorCodeExample(
            value = MemberErrorStatus.class,
            codes = {"MEMBER_NOT_FOUND"}
    )
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshRequest body) {
        try {
            TokenResponse tokens = authService.refreshToken(body.oldRefresh);
            return ApiResponse.onSuccess(TokenSuccessStatus.REFRESH_SUCCESS, tokens);
        } catch (Exception ex) {
            return ApiResponse.onFailure(TokenErrorStatus.INVALID_REFRESH_TOKEN, null);
        }
    }

    @PostMapping("/login/social")
    @Operation(
            summary = "소셜 로그인 및 회원가입",
            description = "가입되어 있는 경우는 액세스/리프레시 주고, 안되어있으면 액세스만 줍니다. 판단은 isSignUpCompleted로"
    )
    public ResponseEntity<ApiResponse<TokenResponse>> socialLogin(@RequestBody SocialLoginRequest body) {
        try {
            TokenResponse tokens = authService.socialLogin(body.provider, body.token);
            
            // 리프레시 토큰이 없으면 약관 동의가 필요한 상태로 간주
            if (tokens.refreshToken == null) {
                return ApiResponse.onSuccess(MemberSuccessStatus.TERMS_AGREEMENT_REQUIRED, tokens);
            }
            
            // 리프레시 토큰이 있으면 정상 로그인
            return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, tokens);
        } catch (Exception ex) {
            return ApiResponse.onFailure(TokenErrorStatus.INVALID_REFRESH_TOKEN, null);
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "로컬 로그인",
            description = "로컬 아이디/비밀번호로 로그인합니다. 가입 완료 시 Access+Refresh, 아니면 방어적 임시 Access만 반환"
    )
    public ResponseEntity<ApiResponse<TokenResponse>> localLogin(@RequestBody LocalLoginRequest body) {
        TokenResponse tokens = authService.localLogin(body);
        if (tokens.refreshToken == null) {
            return ApiResponse.onSuccess(MemberSuccessStatus.TERMS_AGREEMENT_REQUIRED, tokens);
        }
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, tokens);
    }

    @PostMapping("/signup")
    @Operation(
            summary = "로컬 회원가입",
            description = "회원 생성과 동시에 약관 동의를 저장하고 최종 Access+Refresh를 발급합니다."
    )
    public ResponseEntity<ApiResponse<TokenResponse>> signUp(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                            @RequestBody SignUpRequest body) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            body.token = authorization.substring("Bearer ".length()).trim();
        }
        TokenResponse tokens = authService.signUp(body);
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_UP_SUCCESS, tokens);
    }
    @PostMapping("/agreements")
    @Operation(
            summary = "약관 동의 저장",
            description = "필수 약관 검증 후 동의 내역을 저장합니다. 로그인 직후 가입 미완 사용자용."
    )
    public ResponseEntity<ApiResponse<AgreementResponse>> saveAgreements(@AuthenticationPrincipal UserPrincipal principal,
                                                                         @RequestBody AgreementRequest body) {
        Long memberId = principal != null ? principal.getId() : 0L;
        AgreementResponse result = agreementService.agree(memberId, body);
        return ApiResponse.onSuccess(TermSuccessStatus.AGREEMENT_SAVED, result);
    }

}
