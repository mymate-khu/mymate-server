package com.mymate.mymate.notification;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mymate.mymate.notification.entity.NotificationTemplate;
import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;
import com.mymate.mymate.notification.repository.NotificationTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("dev")
@Component
@Order(2)
@RequiredArgsConstructor
public class NotificationTemplateDataInitializer implements CommandLineRunner {

    private final NotificationTemplateRepository templateRepository;

    @Override
    public void run(String... args) {
        Map<NotificationType, TemplateMeta> defaults = Map.of(
                NotificationType.PUZZLE_CREATED, new TemplateMeta(
                        "새로운 퍼즐이 생성되었습니다",
                        "{memberName}님이 '{puzzleTitle}' 퍼즐을 생성했습니다.",
                        Priority.MEDIUM,
                        "/puzzle/{puzzleId}"),
                NotificationType.MATE_REQUEST_RECEIVED, new TemplateMeta(
                        "메이트 요청",
                        "{senderName}님이 메이트 요청을 보냈습니다.",
                        Priority.HIGH,
                        null),
                NotificationType.COMMENT_ADDED, new TemplateMeta(
                        "새 댓글",
                        "{senderName}님이 댓글을 남기셨습니다.",
                        Priority.MEDIUM,
                        "/puzzle/{puzzleId}?comment={commentId}"),
                NotificationType.SYSTEM_MAINTENANCE, new TemplateMeta(
                        "시스템 점검 안내",
                        "{startTime}~{endTime} 시스템 점검이 예정되어 있습니다.",
                        Priority.HIGH,
                        null)
        );

        defaults.forEach((type, meta) -> templateRepository.findByTypeAndIsActiveTrue(type)
                .ifPresentOrElse(existing -> {
                    boolean changed = false;
                    if (!existing.getTitleTemplate().equals(meta.title)) {
                        existing.setTitleTemplate(meta.title);
                        changed = true;
                    }
                    if (!existing.getContentTemplate().equals(meta.content)) {
                        existing.setContentTemplate(meta.content);
                        changed = true;
                    }
                    if (existing.getDefaultPriority() != meta.priority) {
                        existing.setDefaultPriority(meta.priority);
                        changed = true;
                    }
                    if (meta.navigationUrlTemplate != null && (existing.getNavigationUrlTemplate() == null
                            || !existing.getNavigationUrlTemplate().equals(meta.navigationUrlTemplate))) {
                        existing.setNavigationUrlTemplate(meta.navigationUrlTemplate);
                        changed = true;
                    }
                    if (changed) {
                        templateRepository.save(existing);
                        log.info("NOTI:TEMPLATE:INIT:::updated type({})", type);
                    }
                }, () -> {
                    templateRepository.save(NotificationTemplate.builder()
                            .type(type)
                            .titleTemplate(meta.title)
                            .contentTemplate(meta.content)
                            .defaultPriority(meta.priority)
                            .navigationUrlTemplate(meta.navigationUrlTemplate)
                            .isActive(true)
                            .build());
                    log.info("NOTI:TEMPLATE:INIT:::seed created type({})", type);
                }));
    }

    private record TemplateMeta(String title, String content, Priority priority, String navigationUrlTemplate) {}
}


