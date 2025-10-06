package com.mymate.mymate.notification.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mymate.mymate.member.entity.ExpoToken;
import com.mymate.mymate.member.repository.ExpoTokenRepository;
import com.mymate.mymate.notification.dto.FcmMessageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpoPushServiceImpl implements PushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final ExpoTokenRepository expoTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public FcmMessageResponse sendToUser(Long memberId, String title, String body, Object data) {
        List<ExpoToken> tokens = expoTokenRepository.findActiveTokensByMemberId(memberId);
        if (tokens.isEmpty()) {
            return FcmMessageResponse.failure("활성 토큰 없음");
        }
        return sendToMultipleDevices(tokens.stream().map(ExpoToken::getToken).toList(), title, body, data);
    }

    @Override
    public FcmMessageResponse sendToUsers(List<Long> memberIds, String title, String body, Object data) {
        List<ExpoToken> tokens = expoTokenRepository.findActiveTokensByMemberIds(memberIds);
        if (tokens.isEmpty()) {
            return FcmMessageResponse.failure("활성 토큰 없음");
        }
        return sendToMultipleDevices(tokens.stream().map(ExpoToken::getToken).toList(), title, body, data);
    }

    private FcmMessageResponse sendToMultipleDevices(List<String> expoPushTokens, String title, String body, Object data) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Accept", "application/json");

            var payloads = expoPushTokens.stream().map(token -> buildExpoMessage(token, title, body, data)).toList();

            HttpEntity<Object> request = new HttpEntity<>(payloads, headers);
            var response = restTemplate.postForEntity(EXPO_PUSH_URL, request, Map.class);
            log.info("EXPO:RESP status={} body={}", response.getStatusCodeValue(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return FcmMessageResponse.success(payloads.size(), 0);
            } else {
                return FcmMessageResponse.failure("HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.warn("EXPO:SEND:fail err=", e);
            return FcmMessageResponse.failure(e.getMessage());
        }
    }

    private Map<String, Object> buildExpoMessage(String expoToken, String title, String body, Object data) {
        Map<String, Object> m = new HashMap<>();
        m.put("to", expoToken);
        m.put("title", title);
        m.put("body", body);
        // 기본 채널/사운드/우선순위 지정 (필요 시 환경설정으로 치환 가능)
        m.put("channelId", "default");
        m.put("sound", "default");
        m.put("priority", "high");
        if (data instanceof Map<?, ?> map) {
            m.put("data", map);
        }
        return m;
    }
}


