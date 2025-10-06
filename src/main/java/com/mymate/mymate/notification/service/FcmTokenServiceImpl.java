package com.mymate.mymate.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymate.mymate.member.entity.FcmToken;
import com.mymate.mymate.member.repository.FcmTokenRepository;
import com.mymate.mymate.notification.dto.FcmTokenRequest;
import com.mymate.mymate.notification.dto.FcmTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Override
    @Transactional
    public FcmTokenResponse registerToken(Long memberId, FcmTokenRequest request) {
        FcmToken token = fcmTokenRepository
                .findByMemberIdAndToken(memberId, request.getToken())
                .orElseGet(() -> FcmToken.builder()
                        .memberId(memberId)
                        .token(request.getToken())
                        .deviceType(request.getDeviceType())
                        .build());
        token.updateLastUsedAt();
        token = fcmTokenRepository.save(token);
        return getActiveTokens(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public FcmTokenResponse getActiveTokens(Long memberId) {
        List<String> tokens = fcmTokenRepository.findActiveTokensByMemberId(memberId)
                .stream().map(FcmToken::getToken).toList();
        return new FcmTokenResponse(tokens);
    }

    @Override
    @Transactional
    public void deleteToken(Long memberId, String token) {
        fcmTokenRepository.findByMemberIdAndToken(memberId, token)
                .ifPresent(FcmToken::deactivate);
    }
}


