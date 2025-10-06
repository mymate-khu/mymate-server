package com.mymate.mymate.notification.service;

import com.mymate.mymate.notification.dto.FcmTokenRequest;
import com.mymate.mymate.notification.dto.FcmTokenResponse;

public interface FcmTokenService {
    FcmTokenResponse registerToken(Long memberId, FcmTokenRequest request);
    FcmTokenResponse getActiveTokens(Long memberId);
    void deleteToken(Long memberId, String token);
}


