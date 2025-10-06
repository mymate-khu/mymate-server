package com.mymate.mymate.notification.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymate.mymate.member.entity.ExpoToken;
import com.mymate.mymate.member.repository.ExpoTokenRepository;
import com.mymate.mymate.notification.dto.ExpoTokenRequest;
import com.mymate.mymate.notification.dto.ExpoTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpoTokenServiceImpl implements ExpoTokenService {

    private final ExpoTokenRepository expoTokenRepository;

    @Override
    @Transactional
    public ExpoTokenResponse registerToken(Long memberId, ExpoTokenRequest request) {
        try {
            ExpoToken entity = expoTokenRepository
                    .findByMemberIdAndTokenAndDeviceType(memberId, request.getToken(), request.getDeviceType())
                    .orElseGet(() -> ExpoToken.builder()
                            .memberId(memberId)
                            .token(request.getToken())
                            .deviceType(request.getDeviceType())
                            .build());

            entity.updateLastUsedAt();
            expoTokenRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            expoTokenRepository.findByMemberIdAndTokenAndDeviceType(memberId, request.getToken(), request.getDeviceType())
                    .ifPresent(existing -> {
                        existing.updateLastUsedAt();
                        expoTokenRepository.save(existing);
                    });
        }
        return getActiveTokens(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpoTokenResponse getActiveTokens(Long memberId) {
        List<String> tokens = expoTokenRepository.findActiveTokensByMemberId(memberId)
                .stream().map(ExpoToken::getToken).toList();
        return new ExpoTokenResponse(tokens);
    }

    @Override
    @Transactional
    public void deleteToken(Long memberId, String token) {
        expoTokenRepository.findByMemberIdAndToken(memberId, token)
                .ifPresent(ExpoToken::deactivate);
    }
}


