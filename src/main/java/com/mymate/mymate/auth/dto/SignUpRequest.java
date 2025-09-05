package com.mymate.mymate.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignUpRequest {

    @NotBlank
    public String username;

    @NotBlank
    public String passwordEncrypted;

    @Email
    public String email;

    @NotBlank
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
}


