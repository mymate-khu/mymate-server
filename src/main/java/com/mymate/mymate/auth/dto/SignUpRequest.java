package com.mymate.mymate.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignUpRequest {

    // 로컬 전용(소셜에선 미사용)
    public String userId; // 로컬 전용 로그인 ID

    // 로컬 전용(소셜에선 미사용)
    public String passwordEncrypted;

    @Email
    public String email; // 소셜에선 토큰의 이메일 사용

    // 로컬 전용(소셜에선 토큰의 이름 사용)
    public String name;

    // 휴대폰 번호 (인증 완료된 번호)
    @NotBlank
    public String phone;

    // 약관 동의 플래그
    public Boolean agreeService;
    public Boolean agreePrivacy;
    public Boolean agreeAgeOver14;
    public Boolean agreeThirdParty;
    public Boolean agreeMarketing;

    // 소셜 가입 시 임시 토큰(Authorization 대체 전달용)
    public String token;
}


