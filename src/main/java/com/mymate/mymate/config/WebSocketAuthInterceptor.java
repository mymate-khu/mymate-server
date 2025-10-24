package com.mymate.mymate.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mymate.mymate.auth.jwt.JwtProvider;
import com.mymate.mymate.auth.jwt.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private static final String PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            StompCommand command = accessor.getCommand();

            // CONNECT 명령어일 때 인증 처리
            if (StompCommand.CONNECT.equals(command)) {
                String authToken = accessor.getFirstNativeHeader("Authorization");

                if (authToken != null && authToken.startsWith(PREFIX)) {
                    String token = authToken.substring(PREFIX.length());

                    try {
                        if (jwtProvider.validateToken(token)) {
                            UserPrincipal userPrincipal = jwtProvider.getUserPrincipal(token);
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userPrincipal,
                                            null,
                                            userPrincipal.getAuthorities()
                                    );

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);

                            log.info("WS:AUTH:SUCCESS:::userId={}, email={}",
                                    userPrincipal.getId(),
                                    userPrincipal.getEmail());
                        }
                    } catch (Exception e) {
                        log.error("WS:AUTH:FAILED:::error={}", e.getMessage());
                    }
                }
            }
            // 다른 명령어들도 User 정보 유지
            else if (accessor.getUser() == null) {
                // 세션에서 User 정보 복원 시도
                Object user = accessor.getSessionAttributes() != null
                        ? accessor.getSessionAttributes().get("user")
                        : null;

                if (user instanceof UsernamePasswordAuthenticationToken) {
                    accessor.setUser((UsernamePasswordAuthenticationToken) user);
                }
            }
        }

        return message;
    }
}