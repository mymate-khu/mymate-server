package com.mymate.mymate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로컬 로그인 요청", example = """
{
  "userId": "user1",
  "passwordEncrypted": "user1pass"
}
""")
public class LocalLoginRequest {

    @Schema(description = "사용자 ID", example = "user123")
    @NotBlank
    public String userId;

    @Schema(description = "암호화된 비밀번호", example = "encrypted_password_string")
    @NotBlank
    public String passwordEncrypted; // 클라이언트 암호화 명세와 무관히 서버에서 검증
}


