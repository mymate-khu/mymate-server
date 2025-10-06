package com.mymate.mymate.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mymate.mymate.notification.entity.Notification;
import com.mymate.mymate.notification.enums.NotificationStatus;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipient_IdAndStatus(Long recipientId, NotificationStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.status = :toStatus, n.readAt = :now where n.recipient.id = :memberId and n.status = :fromStatus")
    int markAllAsRead(@Param("memberId") Long memberId,
                      @Param("fromStatus") NotificationStatus fromStatus,
                      @Param("toStatus") NotificationStatus toStatus,
                      @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteByExpiresAtBefore(LocalDateTime now);
}


