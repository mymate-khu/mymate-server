package com.mymate.mymate.notification.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            FcmToken entity = fcmTokenRepository
                    .findByMemberIdAndTokenAndDeviceType(memberId, request.getToken(), request.getDeviceType())
                    .orElseGet(() -> FcmToken.builder()
                            .memberId(memberId)
                            .token(request.getToken())
                            .deviceType(request.getDeviceType())
                            .build());

            entity.updateLastUsedAt();
            fcmTokenRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 인한 중복 삽입 경쟁을 방지하기 위한 재조회 후 갱신
            fcmTokenRepository.findByMemberIdAndTokenAndDeviceType(memberId, request.getToken(), request.getDeviceType())
                    .ifPresent(existing -> {
                        existing.updateLastUsedAt();
                        fcmTokenRepository.save(existing);
                    });
        }
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


