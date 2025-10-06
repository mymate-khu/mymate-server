package com.mymate.mymate.web.controller.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.notification.status.NotificationErrorStatus;
import com.mymate.mymate.common.exception.notification.status.NotificationSuccessStatus;
import com.mymate.mymate.notification.dto.NotificationClickRequest;
import com.mymate.mymate.notification.dto.NotificationListResponse;
import com.mymate.mymate.notification.dto.NotificationResponse;
import com.mymate.mymate.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "알림 API", description = "알림 조회 및 상태 변경")
@Validated
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "내 알림 목록 조회")
    @ApiErrorCodeExample(value = NotificationErrorStatus.class, codes = { "INTERNAL_SERVER_ERROR" })
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {
        NotificationListResponse body = notificationService.getNotifications(principal.getId());
        return ApiResponse.onSuccess(NotificationSuccessStatus.NOTIFICATION_LIST_FETCHED, body);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "읽지 않은 알림 개수 조회")
    @ApiErrorCodeExample(value = NotificationErrorStatus.class, codes = { "INTERNAL_SERVER_ERROR" })
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {
        long count = notificationService.getUnreadCount(principal.getId());
        return ApiResponse.onSuccess(NotificationSuccessStatus.UNREAD_COUNT_FETCHED, count);
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "알림 상세 조회")
    @ApiErrorCodeExample(value = NotificationErrorStatus.class, codes = {
            "INVALID_NOTIFICATION_ID", "NOTIFICATION_NOT_FOUND", "NOTIFICATION_ACCESS_DENIED"
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        NotificationResponse body = notificationService.getNotification(notificationId, principal.getId());
        return ApiResponse.onSuccess(NotificationSuccessStatus.NOTIFICATION_DETAIL_FETCHED, body);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리")
    @ApiErrorCodeExample(value = NotificationErrorStatus.class, codes = {
            "INVALID_NOTIFICATION_ID", "NOTIFICATION_NOT_FOUND", "NOTIFICATION_ACCESS_DENIED"
    })
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(notificationId, principal.getId());
        return ApiResponse.onSuccess(NotificationSuccessStatus.NOTIFICATION_READ);
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "모든 알림 읽음 처리")
    @ApiErrorCodeExample(value = NotificationErrorStatus.class, codes = { "INTERNAL_SERVER_ERROR" })
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        int processed = notificationService.markAllAsRead(principal.getId());
        return ApiResponse.onSuccess(NotificationSuccessStatus.ALL_NOTIFICATIONS_READ, processed);
    }

    @PostMapping("/{notificationId}/navigate")
    @Operation(summary = "알림 네비게이션 처리")
    @ApiErrorCodeExample(value = NotificationErrorStatus.class, codes = {
            "INVALID_NOTIFICATION_ID", "NOTIFICATION_NOT_FOUND", "NOTIFICATION_ACCESS_DENIED", "ALREADY_NAVIGATED"
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> navigate(
            @PathVariable("notificationId") Long notificationId,
            @RequestBody NotificationClickRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        NotificationResponse body = notificationService.navigate(notificationId, principal.getId());
        return ApiResponse.onSuccess(NotificationSuccessStatus.NOTIFICATION_NAVIGATED, body);
    }
}


