package com.mymate.mymate.account.dto;

import com.mymate.mymate.account.enums.AccountStatus;
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
@Schema(description = "정산 상태 변경 요청", example = """
{
  "status": "COMPLETED"
}
""")
public class AccountStatusUpdateRequest {

    @NotNull(message = "상태는 필수입니다")
    @Schema(description = "정산 상태", example = "COMPLETED")
    private AccountStatus status;
}