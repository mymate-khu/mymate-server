package com.mymate.mymate.web.controller.notification;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.notification.status.NotificationSuccessStatus;
import com.mymate.mymate.notification.dto.NotificationListResponse;
import com.mymate.mymate.notification.dto.NotificationResponse;
import com.mymate.mymate.notification.mapper.NotificationMapper;
import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.enums.Priority;
import com.mymate.mymate.notification.service.NotificationComposerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/internal/notifications")
@Tag(name = "알림 내부 API", description = "내부 발송/관리용 API")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationComposerService composerService;

    public record SendRequest(Long recipientId,
                              Long senderId,
                              NotificationType type,
                              String title,
                              String content,
                              Priority priority,
                              String navigationUrl,
                              Map<String, Object> data) {}

    public record SendBatchRequest(List<Long> recipientIds,
                                   NotificationType type,
                                   String title,
                                   String content,
                                   Priority priority,
                                   String navigationUrl,
                                   Map<String, Object> data) {}

    @PostMapping("/send")
    @Operation(summary = "단일 알림 발송")
    public ResponseEntity<ApiResponse<NotificationResponse>> send(@RequestBody @Parameter(description = "발송 요청 바디") SendRequest req) {
        var saved = composerService.composeAndSend(
                req.recipientId(), req.senderId(), req.type(), req.title(), req.content(),
                req.priority(), req.navigationUrl(), req.data()
        );
        return ApiResponse.onSuccess(NotificationSuccessStatus.NOTIFICATION_SENT, NotificationMapper.toResponse(saved));
    }

    @PostMapping("/send-batch")
    @Operation(summary = "다중 알림 발송")
    public ResponseEntity<ApiResponse<NotificationListResponse>> sendBatch(
            @RequestBody @Parameter(description = "다중 발송 요청 바디") SendBatchRequest req) {
        var saved = composerService.composeAndSendToUsers(
                req.recipientIds(), req.type(), req.title(), req.content(),
                req.priority(), req.navigationUrl(), req.data()
        );
        return ApiResponse.onSuccess(NotificationSuccessStatus.NOTIFICATION_SENT, NotificationMapper.toListResponse(saved));
    }
}


