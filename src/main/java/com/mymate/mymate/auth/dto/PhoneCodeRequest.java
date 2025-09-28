package com.mymate.mymate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "휴대폰 인증 코드 요청", example = """
{
  "phone": "01012345678"
}
""")
public class PhoneCodeRequest {
    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phone;
}
