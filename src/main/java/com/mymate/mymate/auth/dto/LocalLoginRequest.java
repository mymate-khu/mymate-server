package com.mymate.mymate.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LocalLoginRequest {

    @NotBlank
    public String username;

    @NotBlank
    public String passwordEncrypted; // 클라이언트 암호화 명세와 무관히 서버에서 검증
}


