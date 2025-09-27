package com.mymate.mymate.group.repository;

import com.mymate.mymate.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    List<GroupMember> findByMemberId(Long memberId);
    
    List<GroupMember> findByGroupId(Long groupId);
    
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, Long memberId);
    
    boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.memberId = :memberId")
    List<GroupMember> findGroupsByMemberId(@Param("memberId") Long memberId);
}
