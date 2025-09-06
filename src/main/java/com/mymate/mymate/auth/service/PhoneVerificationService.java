package com.mymate.mymate.auth.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.solapi.sdk.message.service.DefaultMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultMessageService messageService;

    @Value("${solapi.default-from:}")
    private String defaultFrom;

    private static final String KEY_PREFIX = "phone:verify:";
    private static final Duration TTL = Duration.ofMinutes(5);

    public String requestCode(String phone) {
        String code = generateCode();
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, TTL);
        
        // 개발/테스트 환경에서는 실제 SMS 발송하지 않고 인증번호를 반환
        System.out.println("[개발모드] 인증번호 발송: " + phone + " -> " + code);
        
        // 실제 SMS 발송 부분 (유료 서비스이므로 주석처리)
        /*
        try {
            Message message = new Message();
            message.setFrom(defaultFrom);
            message.setTo(phone);
            message.setText("[마이메이트] 인증번호 " + code + " (5분 내 유효)");
            messageService.send(message);
        } catch (Exception e) {
            redisTemplate.delete(KEY_PREFIX + phone);
            throw new RuntimeException("인증코드 발송 실패: " + e.getMessage(), e);
        }
        */
        
        // 개발 모드에서는 인증번호를 반환 (프론트엔드에서 사용)
        return code;
    }

    public boolean verifyCode(String phone, String code) {
        Object saved = redisTemplate.opsForValue().get(KEY_PREFIX + phone);
        if (saved == null || !saved.toString().equals(code)) {
            return false;
        }
        redisTemplate.delete(KEY_PREFIX + phone);
        redisTemplate.opsForValue().set(KEY_PREFIX + "verified:" + phone, "true", TTL);
        return true;
    }

    public boolean isPhoneVerified(String phone) {
        Object verified = redisTemplate.opsForValue().get(KEY_PREFIX + "verified:" + phone);
        return verified != null;
    }

    private String generateCode() {
        int n = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(n);
    }
}
