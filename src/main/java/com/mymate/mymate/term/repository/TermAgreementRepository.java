package com.mymate.mymate.term.repository;

import com.mymate.mymate.term.entity.TermAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermAgreementRepository extends JpaRepository<TermAgreement, Long> {
    Optional<TermAgreement> findByMemberIdAndTermId(Long memberId, Long termId);
    List<TermAgreement> findByMemberId(Long memberId);
}


