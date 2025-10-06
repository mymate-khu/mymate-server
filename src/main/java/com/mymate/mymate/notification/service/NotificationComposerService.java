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

@Service
@RequiredArgsConstructor
public class NotificationComposerService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    @Transactional
    public Notification composeAndSend(Long recipientId,
                                       Long senderId,
                                       NotificationType type,
                                       String title,
                                       String content,
                                       Priority priority,
                                       String navigationUrl,
                                       Map<String, Object> data) {

        Member recipient = memberRepository.findById(recipientId).orElseThrow();
        Member sender = senderId != null ? memberRepository.findById(senderId).orElse(null) : null;

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

        fcmService.sendToUser(recipientId, title, content, data);

        return saved;
    }

    @Transactional
    public List<Notification> composeAndSendToUsers(List<Long> recipientIds,
                                                    NotificationType type,
                                                    String title,
                                                    String content,
                                                    Priority priority,
                                                    String navigationUrl,
                                                    Map<String, Object> data) {

        List<Notification> saved = recipientIds.stream().map(rid -> composeAndSend(
                rid, null, type, title, content, priority, navigationUrl, data
        )).toList();

        return saved;
    }
}


