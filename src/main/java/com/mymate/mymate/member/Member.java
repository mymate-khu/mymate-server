package com.mymate.mymate.member;

import com.mymate.mymate.auth.enums.AuthProvider;
import com.mymate.mymate.common.entity.BaseEntity;
import com.mymate.mymate.member.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_provider_user", columnNames = {"provider", "providerUserId"})
        },
        indexes = {}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(nullable = false, length = 191)
    private String providerUserId;

    @Column(length = 255)
    private String email;

    // 로컬 로그인용 (겸용)
    @Column(length = 40, unique = true)
    private String username;

    @Column(length = 100)
    private String passwordHash;

    @Column(nullable = false)
    private boolean isSignUpCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column
    private LocalDateTime inactive;
}