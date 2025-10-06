package com.mymate.mymate.common.exception.notification.status;

import com.mymate.mymate.common.exception.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum NotificationSuccessStatus implements SuccessResponse {

    // 알림 조회 관련
    NOTIFICATION_LIST_FETCHED(HttpStatus.OK, "NOTI2001", "알림 목록을 조회했습니다."),
    NOTIFICATION_DETAIL_FETCHED(HttpStatus.OK, "NOTI2002", "알림 상세 정보를 조회했습니다."),
    UNREAD_COUNT_FETCHED(HttpStatus.OK, "NOTI2003", "읽지 않은 알림 개수를 조회했습니다."),

    // 알림 상태 변경 관련
    NOTIFICATION_READ(HttpStatus.OK, "NOTI2011", "알림을 읽음 처리했습니다."),
    ALL_NOTIFICATIONS_READ(HttpStatus.OK, "NOTI2012", "모든 알림을 읽음 처리했습니다."),
    NOTIFICATION_NAVIGATED(HttpStatus.OK, "NOTI2013", "알림 네비게이션을 처리했습니다."),

    // 액션 처리 관련
    MATE_REQUEST_ACCEPTED(HttpStatus.OK, "NOTI2021", "메이트 요청을 수락했습니다."),
    MATE_REQUEST_DECLINED(HttpStatus.OK, "NOTI2022", "메이트 요청을 거절했습니다."),

    // 알림 설정 관련
    PREFERENCE_FETCHED(HttpStatus.OK, "NOTI2031", "알림 설정을 조회했습니다."),
    PREFERENCE_UPDATED(HttpStatus.OK, "NOTI2032", "알림 설정을 업데이트했습니다."),
    PREFERENCE_BATCH_UPDATED(HttpStatus.OK, "NOTI2033", "알림 설정을 일괄 업데이트했습니다."),

    // 알림 발송 관련 (내부용)
    NOTIFICATION_SENT(HttpStatus.OK, "NOTI2041", "알림을 발송했습니다."),
    SYSTEM_NOTIFICATION_SENT(HttpStatus.OK, "NOTI2042", "시스템 알림을 발송했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    NotificationSuccessStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getSuccessStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


