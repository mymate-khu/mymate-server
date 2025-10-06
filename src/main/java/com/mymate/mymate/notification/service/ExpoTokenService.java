package com.mymate.mymate.notification.service;

import com.mymate.mymate.notification.dto.ExpoTokenRequest;
import com.mymate.mymate.notification.dto.ExpoTokenResponse;

public interface ExpoTokenService {
    ExpoTokenResponse registerToken(Long memberId, ExpoTokenRequest request);
    ExpoTokenResponse getActiveTokens(Long memberId);
    void deleteToken(Long memberId, String token);
}


