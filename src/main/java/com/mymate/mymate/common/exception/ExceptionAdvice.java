package com.mymate.mymate.common.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.general.status.ErrorStatus;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(
        annotations = { RestController.class },
        basePackages = { "com.tourapi.tourapi.web.controller" }
)
public class ExceptionAdvice {

    /**
     * GeneralException 처리
     * @param exception GeneralException
     * @return ApiResponse - GeneralException
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(GeneralException exception) {
        log.error("COMM:CTRL:GENERAL:::GeneralException msg({})", exception.getStatus().getMessage());
        return ApiResponse.onFailure(exception.getStatus());
    }

    /**
     * Exception 처리
     * @param exception Exception
     * @return ApiResponse - INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception exception) {
        log.error("COMM:CTRL:____:::Exception msg({})", exception.getMessage());
        return ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    /**
     * MethodArgumentTypeMismatchException 처리 - 잘못된 값 입력
     * @param exception MethodArgumentTypeMismatchException
     * @return ApiResponse - BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("ARG_:CTRL:MISMATCH:::MethodArgumentTypeMismatchException msg({})", exception.getMessage());
        return ApiResponse.onFailure(ErrorStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * JwtException 처리
     * @param exception JwtException
     * @return ApiResponse - FORBIDDEN
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<String>> handleJwtException(JwtException exception) {
        log.error("JWT_:CTRL:INVALID:::Exception msg({})", exception.getMessage());
        return ApiResponse.onFailure(ErrorStatus.FORBIDDEN, exception.getMessage());
    }
}
