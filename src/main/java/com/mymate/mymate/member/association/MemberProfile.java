package com.mymate.mymate.member.association;

import com.mymate.mymate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_profile",
       uniqueConstraints = {
               @UniqueConstraint(name = "uk_member_profile_member", columnNames = {"memberId"}),
               @UniqueConstraint(name = "uk_member_profile_nickname", columnNames = {"nickname"})
       }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(length = 40)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(length = 200)
    private String bio;
}


