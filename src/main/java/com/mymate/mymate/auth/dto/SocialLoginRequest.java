package com.mymate.mymate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 로그인 요청", example = """
{
  "provider": "GOOGLE",
  "token": "ya29.a0AfH6SMC..."
}
""")
public class SocialLoginRequest {
    @Schema(description = "소셜 제공자", example = "GOOGLE")
    public String provider;
    
    @Schema(description = "소셜 액세스/ID 토큰", example = "ya29.a0AfH6SMC...")
    public String token;
}

