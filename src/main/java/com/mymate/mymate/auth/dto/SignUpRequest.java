package com.mymate.mymate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원가입 요청", example = """
{
  "userId": "user123",
  "passwordEncrypted": "encrypted_password_string",
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "01012345678",
  "agreeService": true,
  "agreePrivacy": true,
  "agreeAgeOver14": true,
  "agreeThirdParty": false,
  "agreeMarketing": false,
  "token": null
}
""")
public class SignUpRequest {

    @Schema(description = "사용자 ID (로컬 전용)", example = "user123")
    // 로컬 전용(소셜에선 미사용)
    public String userId; // 로컬 전용 로그인 ID

    @Schema(description = "암호화된 비밀번호 (로컬 전용)", example = "encrypted_password_string")
    // 로컬 전용(소셜에선 미사용)
    public String passwordEncrypted;

    @Schema(description = "이메일 주소", example = "user@example.com")
    @Email
    public String email; // 소셜에선 토큰의 이메일 사용

    @Schema(description = "사용자 이름 (로컬 전용)", example = "홍길동")
    // 로컬 전용(소셜에선 토큰의 이름 사용)
    public String name;

    @Schema(description = "휴대폰 번호 (인증 완료된 번호)", example = "01012345678")
    // 휴대폰 번호 (인증 완료된 번호)
    @NotBlank
    public String phone;

    @Schema(description = "서비스 이용약관 동의", example = "true")
    // 약관 동의 플래그
    public Boolean agreeService;
    
    @Schema(description = "개인정보 처리방침 동의", example = "true")
    public Boolean agreePrivacy;
    
    @Schema(description = "14세 이상 확인", example = "true")
    public Boolean agreeAgeOver14;
    
    @Schema(description = "제3자 정보 제공 동의", example = "false")
    public Boolean agreeThirdParty;
    
    @Schema(description = "마케팅 정보 수신 동의", example = "false")
    public Boolean agreeMarketing;

    @Schema(description = "소셜 로그인 임시 토큰", example = "null")
    // 소셜 가입 시 임시 토큰(Authorization 대체 전달용)
    public String token;
}


