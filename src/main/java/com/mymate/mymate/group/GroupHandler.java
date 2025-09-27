package com.mymate.mymate.group;

import com.mymate.mymate.common.exception.ExceptionAdvice;
import com.mymate.mymate.common.exception.general.status.ErrorStatus;
import com.mymate.mymate.group.status.GroupErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GroupHandler extends ExceptionAdvice {

    @Override
    protected void logException(Exception exception, ErrorStatus errorStatus) {
        log.error("Group 도메인 예외 발생: {}", exception.getMessage(), exception);
    }

    @Override
    protected void logException(Exception exception, GroupErrorStatus errorStatus) {
        log.error("Group 도메인 예외 발생: {}", exception.getMessage(), exception);
    }
}
