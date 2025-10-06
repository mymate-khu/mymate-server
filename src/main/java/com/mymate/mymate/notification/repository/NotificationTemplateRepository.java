package com.mymate.mymate.notification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mymate.mymate.notification.entity.NotificationTemplate;
import com.mymate.mymate.notification.enums.NotificationType;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByTypeAndIsActiveTrue(NotificationType type);
}


