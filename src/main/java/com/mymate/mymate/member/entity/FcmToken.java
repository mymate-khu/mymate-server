package com.mymate.mymate.member.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "fcm_token",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "token", "device_type"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Column(name = "device_type", length = 20)
    private String deviceType; // ANDROID, IOS, WEB

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_used_at")
    private java.time.LocalDateTime lastUsedAt;

    public void updateLastUsedAt() {
        this.lastUsedAt = java.time.LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
    }
}


