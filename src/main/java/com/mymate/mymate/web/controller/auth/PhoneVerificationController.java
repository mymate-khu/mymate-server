package com.mymate.mymate.web.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mymate.mymate.auth.dto.PhoneCodeRequest;
import com.mymate.mymate.auth.dto.PhoneCodeVerifyRequest;
import com.mymate.mymate.auth.service.PhoneVerificationService;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiErrorCodeExamples;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.phone.status.PhoneSuccessStatus;
import com.mymate.mymate.common.exception.phone.status.PhoneErrorStatus;
import com.mymate.mymate.common.exception.phone.PhoneHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/phone")
@RequiredArgsConstructor
@Tag(name = "2-3. 휴대폰 인증", description = "휴대폰 인증 요청 및 검증")
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/request-code")
    @Operation(
            summary = "인증번호 요청",
            description = "전화번호로 SMS 인증번호를 발송합니다.",
            tags = {"2. 휴대폰 인증 요청"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PhoneErrorStatus.class,
                    codes = {"VERIFICATION_FAILED", "NOT_VERIFIED"}
            )
    })
    public ResponseEntity<ApiResponse<String>> requestCode(@RequestBody PhoneCodeRequest request) {
        String code = phoneVerificationService.requestCode(request.getPhone());
        return ApiResponse.onSuccess(PhoneSuccessStatus.VERIFICATION_CODE_SENT, code);
    }

    @PostMapping("/verify-code")
    @Operation(
            summary = "인증번호 검증",
            description = "전송된 인증번호를 검증합니다.",
            tags = {"3. 휴대폰 인증 검증"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PhoneErrorStatus.class,
                    codes = {"VERIFICATION_FAILED", "NOT_VERIFIED"}
            )
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestBody PhoneCodeVerifyRequest request) {
        boolean isValid = phoneVerificationService.verifyCode(request.getPhone(), request.getCode());
        if (!isValid) {
            throw new PhoneHandler(PhoneErrorStatus.VERIFICATION_FAILED);
        }
        return ApiResponse.onSuccess(PhoneSuccessStatus.VERIFICATION_SUCCESS, true);
    }

    @GetMapping("/check-verified/{phone}")
    @Operation(
            summary = "전화번호 인증 상태 확인",
            description = "전화번호의 인증 상태를 확인합니다.",
            tags = {"3. 휴대폰 인증 검증"}
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(
                    value = PhoneErrorStatus.class,
                    codes = {"NOT_VERIFIED", "VERIFICATION_FAILED"}
            )
    })
    public ResponseEntity<ApiResponse<Boolean>> checkVerified(@PathVariable String phone) {
        boolean isVerified = phoneVerificationService.isPhoneVerified(phone);
        if (!isVerified) {
            throw new PhoneHandler(PhoneErrorStatus.NOT_VERIFIED);
        }
        return ApiResponse.onSuccess(PhoneSuccessStatus.VERIFICATION_SUCCESS, true);
    }
}

