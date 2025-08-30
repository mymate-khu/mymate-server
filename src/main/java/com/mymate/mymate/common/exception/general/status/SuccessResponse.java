package com.mymate.mymate.common.exception.general.status;

import org.springframework.http.HttpStatus;

public interface SuccessResponse {
    HttpStatus getSuccessStatus();
    String getCode();
    String getMessage();
}
