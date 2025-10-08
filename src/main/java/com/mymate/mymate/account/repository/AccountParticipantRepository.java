package com.mymate.mymate.account.repository;

import com.mymate.mymate.account.entity.AccountParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountParticipantRepository extends JpaRepository<AccountParticipant, Long> {

    // 특정 정산의 모든 참여자 조회
    List<AccountParticipant> findByAccountId(Long accountId);

    // 특정 정산의 특정 참여자 조회
    Optional<AccountParticipant> findByAccountIdAndMemberId(Long accountId, Long memberId);

    // 특정 정산의 참여자 삭제
    void deleteByAccountId(Long accountId);

    // 특정 정산의 특정 참여자 삭제
    void deleteByAccountIdAndMemberId(Long accountId, Long memberId);

    // 특정 정산의 특정 멤버들 삭제
    void deleteByAccountIdAndMemberIdIn(Long accountId, java.util.Set<Long> memberIds);

    // 특정 멤버가 참여한 정산 목록 조회
    @Query("SELECT ap FROM AccountParticipant ap WHERE ap.memberId = :memberId")
    List<AccountParticipant> findByMemberId(@Param("memberId") Long memberId);

    // 특정 정산의 참여자 수 조회
    long countByAccountId(Long accountId);

    // 특정 정산의 지불 완료 참여자 수 조회
    long countByAccountIdAndIsPaidTrue(Long accountId);

    // 특정 정산에 특정 멤버가 참여하는지 확인
    boolean existsByAccountIdAndMemberId(Long accountId, Long memberId);
}