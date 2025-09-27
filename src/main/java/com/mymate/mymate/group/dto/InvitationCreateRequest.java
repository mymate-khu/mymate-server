package com.mymate.mymate.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvitationCreateRequest {

    @NotNull(message = "초대받을 사용자 ID는 필수입니다")
    private Long inviteeId;

    public InvitationCreateRequest(Long inviteeId) {
        this.inviteeId = inviteeId;
    }
}
