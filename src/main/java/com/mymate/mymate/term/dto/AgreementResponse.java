package com.mymate.mymate.term.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "약관 동의 응답", example = """
{
  "message": "약관 동의가 완료되었습니다.",
  "missingRequired": false,
  "missingService": false,
  "missingPrivacy": false,
  "missingAgeOver14": false
}
""")
public class AgreementResponse {

    @Schema(description = "응답 메시지", example = "약관 동의가 완료되었습니다.")
    private String message;

    @Schema(description = "필수 항목 누락 여부", example = "false")
    // 부족한 필수 항목이 있을 경우 어떤 항목이 부족한지 전달
    private boolean missingRequired;

    @Schema(description = "서비스 이용약관 누락 여부", example = "false")
    private boolean missingService;
    
    @Schema(description = "개인정보 처리방침 누락 여부", example = "false")
    private boolean missingPrivacy;
    
    @Schema(description = "14세 이상 확인 누락 여부", example = "false")
    private boolean missingAgeOver14;
}


