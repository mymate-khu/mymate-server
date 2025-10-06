package com.mymate.mymate.member.repository;

import com.mymate.mymate.member.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByMemberIdAndToken(Long memberId, String token);

    Optional<FcmToken> findByMemberIdAndTokenAndDeviceType(Long memberId, String token, String deviceType);

    List<FcmToken> findByMemberIdAndIsActiveTrue(Long memberId);

    @Query("SELECT ft FROM FcmToken ft WHERE ft.memberId = :memberId AND ft.isActive = true")
    List<FcmToken> findActiveTokensByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT ft FROM FcmToken ft WHERE ft.memberId IN :memberIds AND ft.isActive = true")
    List<FcmToken> findActiveTokensByMemberIds(@Param("memberIds") List<Long> memberIds);

    void deleteByMemberId(Long memberId);

    void deleteByToken(String token);
}


