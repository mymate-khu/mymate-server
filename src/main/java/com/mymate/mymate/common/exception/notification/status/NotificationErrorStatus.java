package com.mymate.mymate.common.exception.notification.status;

import com.mymate.mymate.common.exception.ExplainError;
import com.mymate.mymate.common.exception.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum NotificationErrorStatus implements ErrorResponse {

    // 알림 조회 관련
    @ExplainError("알림을 찾을 수 없음")
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI4001", "알림을 찾을 수 없습니다."),
    @ExplainError("알림 접근 권한 없음")
    NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "NOTI4002", "해당 알림에 대한 권한이 없습니다."),
    @ExplainError("유효하지 않은 알림 ID")
    INVALID_NOTIFICATION_ID(HttpStatus.BAD_REQUEST, "NOTI4003", "유효하지 않은 알림 ID입니다."),

    // 알림 상태 변경 관련
    @ExplainError("잘못된 상태 전환")
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "NOTI4004", "잘못된 상태 전환입니다."),
    @ExplainError("이미 네비게이션 처리된 알림")
    ALREADY_NAVIGATED(HttpStatus.BAD_REQUEST, "NOTI4005", "이미 네비게이션 처리된 알림입니다."),

    // 액션 처리 관련
    @ExplainError("메이트 요청을 찾을 수 없음")
    MATE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI4006", "메이트 요청을 찾을 수 없습니다."),
    @ExplainError("이미 처리된 메이트 요청")
    MATE_REQUEST_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "NOTI4007", "이미 처리된 메이트 요청입니다."),
    @ExplainError("유효하지 않은 액션 유형")
    INVALID_ACTION_TYPE(HttpStatus.BAD_REQUEST, "NOTI4008", "유효하지 않은 액션 유형입니다."),

    // 알림 설정 관련
    @ExplainError("알림 설정을 찾을 수 없음")
    PREFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI4009", "알림 설정을 찾을 수 없습니다."),
    @ExplainError("유효하지 않은 알림 유형")
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "NOTI4010", "유효하지 않은 알림 유형입니다."),
    @ExplainError("방해 금지 시간 형식 오류")
    INVALID_QUIET_HOURS_FORMAT(HttpStatus.BAD_REQUEST, "NOTI4011", "방해 금지 시간 형식이 올바르지 않습니다."),

    // 알림 발송 관련
    @ExplainError("수신자를 찾을 수 없음")
    RECIPIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI4012", "수신자를 찾을 수 없습니다."),
    @ExplainError("알림 발송 실패")
    NOTIFICATION_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "NOTI4013", "알림 발송에 실패했습니다."),
    @ExplainError("알림 템플릿을 찾을 수 없음")
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI4014", "알림 템플릿을 찾을 수 없습니다."),

    // 시스템 관련
    @ExplainError("서버 내부 오류")
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTI4999", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    NotificationErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getErrorStatus() {
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


