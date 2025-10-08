package com.mymate.mymate.account.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "정산 상태")
public enum AccountStatus {
    @Schema(description = "정산 미완료")
    PENDING,
    
    @Schema(description = "정산 완료")
    COMPLETED
}