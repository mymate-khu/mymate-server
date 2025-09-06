package com.mymate.mymate.auth.service;

import com.mymate.mymate.auth.dto.TokenResponse;
import com.mymate.mymate.auth.dto.LocalLoginRequest;
import com.mymate.mymate.auth.dto.SignUpRequest;
import com.mymate.mymate.member.enums.Role;

public interface AuthService {

    record TokenPair(String accessToken, String refreshToken, String sessionId, String familyId) {}

    TokenPair issueTokensOnLogin(Long id, String email, String name, Role role, boolean isSignUpCompleted);
    
    TokenResponse socialLogin(String provider, String token);
    TokenResponse localLogin(LocalLoginRequest request);
    TokenResponse signUp(SignUpRequest request);
    
    TokenResponse refreshToken(String oldRefreshToken);
}
