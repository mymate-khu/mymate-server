package com.mymate.mymate.term.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreementResponse {

    private boolean success;

    private String message;

    // 부족한 필수 항목이 있을 경우 어떤 항목이 부족한지 전달
    private boolean missingRequired;

    private boolean missingService;
    private boolean missingPrivacy;
    private boolean missingAgeOver14;
}


