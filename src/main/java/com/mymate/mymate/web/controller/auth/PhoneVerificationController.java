package com.mymate.mymate.web.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mymate.mymate.auth.dto.PhoneCodeRequest;
import com.mymate.mymate.auth.dto.PhoneCodeVerifyRequest;
import com.mymate.mymate.auth.service.PhoneVerificationService;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.phone.status.PhoneSuccessStatus;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/phone")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/request-code")
    public ResponseEntity<ApiResponse<String>> requestCode(@RequestBody PhoneCodeRequest request) {
        String code = phoneVerificationService.requestCode(request.phone);
        return ApiResponse.onSuccess(PhoneSuccessStatus.VERIFICATION_CODE_SENT, code);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@RequestBody PhoneCodeVerifyRequest request) {
        boolean isValid = phoneVerificationService.verifyCode(request.phone, request.code);
        return ApiResponse.onSuccess(PhoneSuccessStatus.VERIFICATION_SUCCESS, isValid);
    }

    @GetMapping("/check-verified/{phone}")
    public ResponseEntity<ApiResponse<Boolean>> checkVerified(@PathVariable String phone) {
        boolean isVerified = phoneVerificationService.isPhoneVerified(phone);
        return ApiResponse.onSuccess(PhoneSuccessStatus.VERIFICATION_SUCCESS, isVerified);
    }
}

