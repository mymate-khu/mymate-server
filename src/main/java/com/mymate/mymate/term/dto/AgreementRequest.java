package com.mymate.mymate.term.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreementRequest {

    @NotNull
    private Boolean agreeService;

    @NotNull
    private Boolean agreePrivacy;

    @NotNull
    private Boolean agreeAgeOver14;

    private Boolean agreeThirdParty;

    private Boolean agreeMarketing;

    // 최신 버전 검증 옵션: true인 경우, 서버의 최신 버전과 비교하여 검증
    private Boolean verifyLatestVersion;
}


