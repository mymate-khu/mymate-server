package com.mymate.mymate.rulebook.repository;

import com.mymate.mymate.rulebook.entity.Rulebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RulebookRepository extends JpaRepository<Rulebook, Long> {

    // 특정 그룹의 모든 룰북 조회 (최신순)
    List<Rulebook> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    // 특정 그룹의 특정 룰북 조회 (권한 확인용)
    Optional<Rulebook> findByIdAndGroupId(Long id, Long groupId);

    // 특정 그룹의 룰북 개수 조회
    long countByGroupId(Long groupId);
}