package com.mymate.mymate.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;
import com.mymate.mymate.notification.entity.Notification;
import com.mymate.mymate.notification.enums.NotificationStatus;
import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;
import com.mymate.mymate.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationComposerServiceImpl implements NotificationComposerService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateService templateService;
    private final FcmService fcmService;

    @Override
    @Transactional
    public Notification composeAndSend(Long recipientId,
                                       Long senderId,
                                       NotificationType type,
                                       String title,
                                       String content,
                                       Priority priority,
                                       String navigationUrl,
                                       Map<String, Object> data) {

        log.info("NOTI:COMPOSE:start recipientId={} type={} priority={}", recipientId, type, priority);
        Member recipient = memberRepository.findById(recipientId).orElseThrow();
        Member sender = senderId != null ? memberRepository.findById(senderId).orElse(null) : null;

        if (title == null || content == null) {
            NotificationTemplateService.TemplateResult tr = templateService.render(type, data);
            title = title != null ? title : tr.title();
            content = content != null ? content : tr.content();
            navigationUrl = navigationUrl != null ? navigationUrl : tr.navigationUrl();
            log.debug("NOTI:COMPOSE:template-applied title='{}' nav='{}'", title, navigationUrl);
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .title(title)
                .content(content)
                .priority(priority != null ? priority : Priority.MEDIUM)
                .status(NotificationStatus.UNREAD)
                .navigationUrl(navigationUrl)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("NOTI:SAVE:notification id={} recipient={} type={}", saved.getId(), recipientId, type);

        var fcmResp = fcmService.sendToUser(recipientId, title, content, data);
        if (fcmResp.isSuccess()) {
            log.info("FCM:SEND:ok id={} msgId={} succ={} fail={}", saved.getId(), fcmResp.getMessageId(), fcmResp.getSuccessCount(), fcmResp.getFailureCount());
        } else {
            log.warn("FCM:SEND:fail id={} err={}", saved.getId(), fcmResp.getErrorMessage());
        }

        return saved;
    }

    @Override
    @Transactional
    public List<Notification> composeAndSendToUsers(List<Long> recipientIds,
                                                    NotificationType type,
                                                    String title,
                                                    String content,
                                                    Priority priority,
                                                    String navigationUrl,
                                                    Map<String, Object> data) {

        log.info("NOTI:BATCH:compose count={} type={}", recipientIds.size(), type);
        List<Notification> saved = recipientIds.stream().map(rid -> composeAndSend(
                rid, null, type, title, content, priority, navigationUrl, data
        )).toList();
        log.info("NOTI:BATCH:done saved={} type={}", saved.size(), type);
        return saved;
    }
}


