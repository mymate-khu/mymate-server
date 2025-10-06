package com.mymate.mymate.notification.service;

import com.google.firebase.messaging.*;
import com.mymate.mymate.member.entity.FcmToken;
import com.mymate.mymate.member.repository.FcmTokenRepository;
import com.mymate.mymate.notification.dto.FcmMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    @Override
    public FcmMessageResponse sendToDevice(String token, String title, String body, Object data) {
        try {
            Message message = createMessage(token, title, body, data);
            String response = firebaseMessaging.send(message);
            log.info("FCM 단일 전송 성공 token={}, res={}", token, response);
            return FcmMessageResponse.success(response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 단일 전송 실패 token={} err={}", token, e.getMessage());
            return FcmMessageResponse.failure(e.getMessage());
        }
    }

    @Override
    public FcmMessageResponse sendToMultipleDevices(List<String> tokens, String title, String body, Object data) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .putAllData(convertDataToMap(data))
                    .build();
            BatchResponse resp = firebaseMessaging.sendMulticast(message);
            log.info("FCM 멀티 전송 완료 success={}, failure={}", resp.getSuccessCount(), resp.getFailureCount());
            return FcmMessageResponse.success(resp.getSuccessCount(), resp.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티 전송 실패 err=", e);
            return FcmMessageResponse.failure(e.getMessage());
        }
    }

    @Override
    public FcmMessageResponse sendToUser(Long memberId, String title, String body, Object data) {
        List<FcmToken> tokens = fcmTokenRepository.findActiveTokensByMemberId(memberId);
        if (tokens.isEmpty()) {
            return FcmMessageResponse.failure("활성 토큰 없음");
        }
        List<String> tokenStrings = tokens.stream().map(FcmToken::getToken).toList();
        return sendToMultipleDevices(tokenStrings, title, body, data);
    }

    @Override
    public FcmMessageResponse sendToUsers(List<Long> memberIds, String title, String body, Object data) {
        List<FcmToken> tokens = fcmTokenRepository.findActiveTokensByMemberIds(memberIds);
        if (tokens.isEmpty()) {
            return FcmMessageResponse.failure("활성 토큰 없음");
        }
        List<String> tokenStrings = tokens.stream().map(FcmToken::getToken).toList();
        return sendToMultipleDevices(tokenStrings, title, body, data);
    }

    @Override
    public FcmMessageResponse sendToTopic(String topic, String title, String body, Object data) {
        try {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .putAllData(convertDataToMap(data))
                    .build();
            String response = firebaseMessaging.send(message);
            log.info("FCM 토픽 전송 성공 topic={}, res={}", topic, response);
            return FcmMessageResponse.success(response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 토픽 전송 실패 topic={} err=", topic, e);
            return FcmMessageResponse.failure(e.getMessage());
        }
    }

    private Message createMessage(String token, String title, String body, Object data) {
        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .putAllData(convertDataToMap(data))
                .build();
    }

    private Map<String, String> convertDataToMap(Object data) {
        if (data == null) return Map.of();
        return Map.of("data", data.toString());
    }
}


