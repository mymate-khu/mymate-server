package com.mymate.mymate.member.repository;

import com.mymate.mymate.member.entity.ExpoToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpoTokenRepository extends JpaRepository<ExpoToken, Long> {

    Optional<ExpoToken> findByMemberIdAndToken(Long memberId, String token);

    Optional<ExpoToken> findByMemberIdAndTokenAndDeviceType(Long memberId, String token, String deviceType);

    List<ExpoToken> findByMemberIdAndIsActiveTrue(Long memberId);

    @Query("SELECT t FROM ExpoToken t WHERE t.memberId = :memberId AND t.isActive = true")
    List<ExpoToken> findActiveTokensByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT t FROM ExpoToken t WHERE t.memberId IN :memberIds AND t.isActive = true")
    List<ExpoToken> findActiveTokensByMemberIds(@Param("memberIds") List<Long> memberIds);

    void deleteByMemberId(Long memberId);

    void deleteByToken(String token);
}
