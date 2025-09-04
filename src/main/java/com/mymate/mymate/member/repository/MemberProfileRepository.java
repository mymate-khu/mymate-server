package com.mymate.mymate.member.repository;

import com.mymate.mymate.member.association.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    Optional<MemberProfile> findByMemberId(Long memberId);
    boolean existsByNickname(String nickname);
}


