package com.mymate.mymate.group.dto;

import com.mymate.mymate.group.entity.Invitation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class InvitationResponse {

    private Long id;
    private Long groupId;
    private String groupName;
    private Long inviterId;
    private String inviterName;
    private Long inviteeId;
    private LocalDateTime expiresAt;
    private Invitation.InvitationStatus status;
    private LocalDateTime createdAt;

    public InvitationResponse(Invitation invitation, String groupName, String inviterName) {
        this.id = invitation.getId();
        this.groupId = invitation.getGroupId();
        this.groupName = groupName;
        this.inviterId = invitation.getInviterId();
        this.inviterName = inviterName;
        this.inviteeId = invitation.getInviteeId();
        this.expiresAt = invitation.getExpiresAt();
        this.status = invitation.getStatus();
        this.createdAt = invitation.getCreatedAt();
    }
}
