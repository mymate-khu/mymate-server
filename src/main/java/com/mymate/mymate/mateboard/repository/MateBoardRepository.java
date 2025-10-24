package com.mymate.mymate.mateboard.repository;

import com.mymate.mymate.mateboard.entity.MateBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateBoardRepository extends JpaRepository<MateBoard, Long> {

    // 그룹별 메이트보드 목록 조회 (만료되지 않은 것만, 최신순)
    @Query("SELECT mb FROM MateBoard mb WHERE mb.groupId = :groupId AND mb.expiresAt > :now ORDER BY mb.createdAt DESC")
    Page<MateBoard> findByGroupIdAndNotExpired(@Param("groupId") Long groupId, @Param("now") LocalDateTime now, Pageable pageable);

    // 그룹별 메이트보드 목록 조회 (만료되지 않은 것만, 본인 메이트보드 제외, 최신순)
    @Query("SELECT mb FROM MateBoard mb WHERE mb.groupId = :groupId AND mb.expiresAt > :now AND mb.memberId != :memberId ORDER BY mb.createdAt DESC")
    Page<MateBoard> findByGroupIdAndNotExpiredAndNotMemberId(@Param("groupId") Long groupId, @Param("now") LocalDateTime now, @Param("memberId") Long memberId, Pageable pageable);

    // 그룹별 메이트보드 목록 조회 (만료되지 않은 것만, List 형태)
    @Query("SELECT mb FROM MateBoard mb WHERE mb.groupId = :groupId AND mb.expiresAt > :now ORDER BY mb.createdAt DESC")
    List<MateBoard> findByGroupIdAndNotExpiredList(@Param("groupId") Long groupId, @Param("now") LocalDateTime now);

    // 특정 멤버가 작성한 메이트보드 조회 (만료되지 않은 것만)
    @Query("SELECT mb FROM MateBoard mb WHERE mb.memberId = :memberId AND mb.expiresAt > :now ORDER BY mb.createdAt DESC")
    List<MateBoard> findByMemberIdAndNotExpired(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    // 만료된 메이트보드 조회
    @Query("SELECT mb FROM MateBoard mb WHERE mb.expiresAt <= :now")
    List<MateBoard> findExpiredMateBoards(@Param("now") LocalDateTime now);

    // 만료된 메이트보드 삭제
    @Modifying
    @Query("DELETE FROM MateBoard mb WHERE mb.expiresAt <= :now")
    int deleteExpiredMateBoards(@Param("now") LocalDateTime now);

    // 특정 메이트보드가 해당 그룹에 속하는지 확인
    @Query("SELECT mb FROM MateBoard mb WHERE mb.id = :mateBoardId AND mb.groupId = :groupId")
    Optional<MateBoard> findByIdAndGroupId(@Param("mateBoardId") Long mateBoardId, @Param("groupId") Long groupId);

    // 특정 메이트보드의 작성자 확인
    @Query("SELECT mb FROM MateBoard mb WHERE mb.id = :mateBoardId AND mb.memberId = :memberId")
    Optional<MateBoard> findByIdAndMemberId(@Param("mateBoardId") Long mateBoardId, @Param("memberId") Long memberId);

    // 특정 멤버의 특정 날짜 메이트보드 조회 (하루에 하나만)
    @Query("SELECT mb FROM MateBoard mb WHERE mb.memberId = :memberId AND mb.createdAt >= :startOfDay AND mb.createdAt < :endOfDay")
    Optional<MateBoard> findByMemberIdAndDate(@Param("memberId") Long memberId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}