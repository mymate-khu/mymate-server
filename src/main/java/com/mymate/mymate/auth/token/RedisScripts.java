package com.mymate.mymate.auth.token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisScripts {

    @Bean
    public DefaultRedisScript<String> rotateScript() {
        String script = "\n" +
                "-- KEYS[1]=rt:index:{oldHash}\n" +
                "-- KEYS[2]=rt:index:{newHash}\n" +
                "-- KEYS[3]=rt:session:{uid}:{sid}\n" +
                "-- ARGV[1]=ttlSeconds, ARGV[2]=newTokenHash, ARGV[3]=uid, ARGV[4]=sid, ARGV[5]=familyId\n" +
                "if (redis.call('EXISTS', KEYS[1]) == 0) then return 'MISSING_OLD' end\n" +
                "local rotated = redis.call('HGET', KEYS[1], 'rotated')\n" +
                "if (rotated == '1') then return 'REUSED' end\n" +
                "-- 새 토큰 인덱스에 모든 정보 저장\n" +
                "redis.call('HSET', KEYS[2], 'rotated', '0')\n" +
                "redis.call('HSET', KEYS[2], 'uid', ARGV[3])\n" +
                "redis.call('HSET', KEYS[2], 'sid', ARGV[4])\n" +
                "redis.call('HSET', KEYS[2], 'familyId', ARGV[5])\n" +
                "redis.call('EXPIRE', KEYS[2], ARGV[1])\n" +
                "-- 세션 정보 업데이트\n" +
                "redis.call('HSET', KEYS[3], 'tokenHash', ARGV[2])\n" +
                "redis.call('EXPIRE', KEYS[3], ARGV[1])\n" +
                "-- 기존 토큰 삭제\n" +
                "redis.call('HSET', KEYS[1], 'rotated', '1')\n" +
                "redis.call('DEL', KEYS[1])\n" +
                "return 'OK'\n";
        DefaultRedisScript<String> rs = new DefaultRedisScript<>();
        rs.setScriptText(script);
        rs.setResultType(String.class);
        return rs;
    }
}


