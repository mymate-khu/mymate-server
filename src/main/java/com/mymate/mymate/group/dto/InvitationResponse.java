package com.mymate.mymate.group.dto;

import com.mymate.mymate.group.entity.Invitation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "그룹 초대 응답", example = """
{
  "id": 1,
  "groupId": 1,
  "groupName": "우리 가족",
  "inviterId": 123,
  "inviterName": "홍길동",
  "inviteeId": 456,
  "expiresAt": "2024-01-22T10:30:00",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00"
}
""")
public class InvitationResponse {

    @Schema(description = "초대 ID", example = "1")
    private Long id;
    
    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;
    
    @Schema(description = "그룹명", example = "우리 가족")
    private String groupName;
    
    @Schema(description = "초대자 ID", example = "123")
    private Long inviterId;
    
    @Schema(description = "초대자 이름", example = "홍길동")
    private String inviterName;
    
    @Schema(description = "초대받은 사용자 ID", example = "456")
    private Long inviteeId;
    
    @Schema(description = "초대받은 사용자 이름", example = "김철수")
    private String inviteeName;
    
    @Schema(description = "초대받은 사용자 로그인 ID", example = "SZZYDE770")
    private String inviteeMemberLoginId;
    
    @Schema(description = "만료일시", example = "2024-01-22T10:30:00")
    private LocalDateTime expiresAt;
    
    @Schema(description = "초대 상태", example = "PENDING")
    private Invitation.InvitationStatus status;
    
    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
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

    public InvitationResponse(Invitation invitation, String groupName, String inviterName, String inviteeName, String inviteeMemberLoginId) {
        this.id = invitation.getId();
        this.groupId = invitation.getGroupId();
        this.groupName = groupName;
        this.inviterId = invitation.getInviterId();
        this.inviterName = inviterName;
        this.inviteeId = invitation.getInviteeId();
        this.inviteeName = inviteeName;
        this.inviteeMemberLoginId = inviteeMemberLoginId;
        this.expiresAt = invitation.getExpiresAt();
        this.status = invitation.getStatus();
        this.createdAt = invitation.getCreatedAt();
    }
}
