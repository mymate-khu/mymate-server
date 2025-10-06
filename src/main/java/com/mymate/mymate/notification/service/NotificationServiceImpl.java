package com.mymate.mymate.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymate.mymate.common.exception.notification.NotificationHandler;
import com.mymate.mymate.common.exception.notification.status.NotificationErrorStatus;
import com.mymate.mymate.notification.dto.NotificationListResponse;
import com.mymate.mymate.notification.dto.NotificationResponse;
import com.mymate.mymate.notification.entity.Notification;
import com.mymate.mymate.notification.enums.NotificationStatus;
import com.mymate.mymate.notification.mapper.NotificationMapper;
import com.mymate.mymate.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationListResponse getNotifications(Long memberId) {
        List<Notification> list = notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(memberId);
        return NotificationMapper.toListResponse(list);
    }

    @Override
    public NotificationResponse getNotification(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationHandler(NotificationErrorStatus.NOTIFICATION_NOT_FOUND));
        if (!notification.getRecipient().getId().equals(memberId)) {
            throw new NotificationHandler(NotificationErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }
        return NotificationMapper.toResponse(notification);
    }

    @Override
    public long getUnreadCount(Long memberId) {
        return notificationRepository.countByRecipient_IdAndStatus(memberId, NotificationStatus.UNREAD);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationHandler(NotificationErrorStatus.NOTIFICATION_NOT_FOUND));
        if (!notification.getRecipient().getId().equals(memberId)) {
            throw new NotificationHandler(NotificationErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }
        if (notification.getStatus() == NotificationStatus.UNREAD) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }
    }

    @Override
    @Transactional
    public int markAllAsRead(Long memberId) {
        return notificationRepository.markAllAsRead(
                memberId, NotificationStatus.UNREAD, NotificationStatus.READ, LocalDateTime.now());
    }

    @Override
    @Transactional
    public NotificationResponse navigate(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationHandler(NotificationErrorStatus.NOTIFICATION_NOT_FOUND));
        if (!notification.getRecipient().getId().equals(memberId)) {
            throw new NotificationHandler(NotificationErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }
        if (notification.getStatus() == NotificationStatus.NAVIGATED) {
            throw new NotificationHandler(NotificationErrorStatus.ALREADY_NAVIGATED);
        }
        notification.setStatus(NotificationStatus.NAVIGATED);
        notification.setNavigatedAt(LocalDateTime.now());
        return NotificationMapper.toResponse(notification);
    }
}


