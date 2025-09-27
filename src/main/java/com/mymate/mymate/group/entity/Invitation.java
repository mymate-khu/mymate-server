package com.mymate.mymate.group.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "inviter_id", nullable = false)
    private Long inviterId;

    @Column(name = "invitee_id", nullable = false)
    private Long inviteeId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Builder
    public Invitation(Long groupId, Long inviterId, Long inviteeId, LocalDateTime expiresAt) {
        this.groupId = groupId;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.expiresAt = expiresAt;
        this.status = InvitationStatus.PENDING;
    }

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void cancel() {
        this.status = InvitationStatus.CANCELED;
    }

    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isPending() {
        return this.status == InvitationStatus.PENDING;
    }

    public enum InvitationStatus {
        PENDING, ACCEPTED, EXPIRED, CANCELED
    }
}
