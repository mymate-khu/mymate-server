package com.mymate.mymate.group.repository;

import com.mymate.mymate.group.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    List<Invitation> findByInviteeIdAndStatus(Long inviteeId, Invitation.InvitationStatus status);
    
    Optional<Invitation> findByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, Invitation.InvitationStatus status);
    
    boolean existsByGroupIdAndInviteeIdAndStatus(Long groupId, Long inviteeId, Invitation.InvitationStatus status);
    
    @Query("SELECT i FROM Invitation i WHERE i.expiresAt < :now AND i.status = 'PENDING'")
    List<Invitation> findExpiredInvitations(@Param("now") LocalDateTime now);
    
    @Query("SELECT i FROM Invitation i WHERE i.inviteeId = :inviteeId AND i.status = 'PENDING'")
    List<Invitation> findPendingInvitationsByInviteeId(@Param("inviteeId") Long inviteeId);
}
