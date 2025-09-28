package com.mymate.mymate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "그룹 초대 생성 요청", example = """
{
  "inviteeIdentifier": "SZZYDE770"
}
""")
public class InvitationCreateRequest {

    @NotBlank(message = "초대받을 사용자 식별자는 필수입니다")
    @Schema(description = "초대받을 사용자 식별자 (사용자 ID 또는 이메일)", example = "SZZYDE770", required = true)
    private String inviteeIdentifier;  // 사용자 ID 또는 이메일 (예: "SZZYDE770" 또는 "user@gmail.com")

    public InvitationCreateRequest(String inviteeIdentifier) {
        this.inviteeIdentifier = inviteeIdentifier;
    }
}
