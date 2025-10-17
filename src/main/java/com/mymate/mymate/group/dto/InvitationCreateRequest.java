package com.mymate.mymate.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "그룹 초대 생성 요청", example = """
{
  "inviteeIdentifiers": ["SZZYDE770", "user@gmail.com", "ABC123"]
}
""")
public class InvitationCreateRequest {

    @NotEmpty(message = "초대받을 사용자 식별자 목록은 필수입니다")
    @Schema(description = "초대받을 사용자 식별자 목록 (사용자 ID 또는 이메일)", example = "[\"SZZYDE770\", \"user@gmail.com\"]", required = true)
    @JsonProperty("inviteeIdentifiers")
    private List<String> inviteeIdentifiers;  // 사용자 ID 또는 이메일 목록

    public InvitationCreateRequest(List<String> inviteeIdentifiers) {
        this.inviteeIdentifiers = inviteeIdentifiers;
    }
}
