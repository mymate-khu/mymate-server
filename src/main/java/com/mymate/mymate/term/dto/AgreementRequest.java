package com.mymate.mymate.term.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "약관 동의 요청", example = """
{
  "agreeService": true,
  "agreePrivacy": true,
  "agreeAgeOver14": true,
  "agreeThirdParty": false,
  "agreeMarketing": false,
  "verifyLatestVersion": true
}
""")
public class AgreementRequest {

    @NotNull
    @Schema(description = "서비스 이용약관 동의", example = "true")
    private Boolean agreeService;

    @NotNull
    @Schema(description = "개인정보 처리방침 동의", example = "true")
    private Boolean agreePrivacy;

    @NotNull
    @Schema(description = "14세 이상 확인", example = "true")
    private Boolean agreeAgeOver14;

    @Schema(description = "제3자 정보 제공 동의", example = "false")
    private Boolean agreeThirdParty;

    @Schema(description = "마케팅 정보 수신 동의", example = "false")
    private Boolean agreeMarketing;

    @Schema(description = "최신 버전 검증 여부", example = "true")
    // 최신 버전 검증 옵션: true인 경우, 서버의 최신 버전과 비교하여 검증
    private Boolean verifyLatestVersion;
}


