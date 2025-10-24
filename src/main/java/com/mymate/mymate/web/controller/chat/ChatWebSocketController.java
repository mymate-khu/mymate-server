package com.mymate.mymate.web.controller.chat;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.chat.dto.ChatMessageRequest;
import com.mymate.mymate.chat.dto.ChatMessageResponse;
import com.mymate.mymate.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request,
                            SimpMessageHeaderAccessor headerAccessor,
                            Principal principal) {
        try {
            // Principal에서 사용자 정보 추출
            Long memberId = extractMemberId(headerAccessor, principal);

            if (memberId == null) {
                log.error("WS:MESSAGE:ERROR:::인증되지 않은 사용자");
                messagingTemplate.convertAndSend(
                        "/user/queue/errors",
                        "인증이 필요합니다. 다시 연결해주세요."
                );
                return;
            }

            log.info("WS:MESSAGE:RECEIVED:::chatRoomId={}, senderId={}",
                    request.getChatRoomId(), memberId);

            ChatMessageResponse response = chatService.sendMessage(memberId, request);

            // 채팅방의 모든 참여자에게 메시지 전송
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + request.getChatRoomId(),
                    response
            );

        } catch (Exception e) {
            log.error("WS:MESSAGE:ERROR:::error={}", e.getMessage(), e);

            messagingTemplate.convertAndSend(
                    "/user/queue/errors",
                    "메시지 전송에 실패했습니다: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingNotification notification,
                             SimpMessageHeaderAccessor headerAccessor,
                             Principal principal) {
        try {
            Long memberId = extractMemberId(headerAccessor, principal);
            String memberName = extractMemberName(headerAccessor, principal);

            if (memberId == null) {
                log.error("WS:TYPING:ERROR:::인증되지 않은 사용자");
                return;
            }

            log.debug("WS:TYPING:::chatRoomId={}, userId={}, isTyping={}",
                    notification.getChatRoomId(), memberId, notification.isTyping());

            // 타이핑 상태를 다른 참여자들에게 전송 (자신 제외)
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + notification.getChatRoomId() + "/typing",
                    new TypingResponse(memberId, memberName, notification.isTyping())
            );
        } catch (Exception e) {
            log.error("WS:TYPING:ERROR:::error={}", e.getMessage());
        }
    }

    /**
     * Principal과 HeaderAccessor에서 Member ID를 추출하는 헬퍼 메서드
     */
    private Long extractMemberId(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        try {
            // 1. headerAccessor의 User에서 추출 시도
            if (headerAccessor != null && headerAccessor.getUser() != null) {
                Object user = headerAccessor.getUser();

                if (user instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) user;
                    Object principalObj = auth.getPrincipal();

                    if (principalObj instanceof UserPrincipal) {
                        UserPrincipal userPrincipal = (UserPrincipal) principalObj;
                        log.debug("WS:AUTH:EXTRACTED:::memberId={}, email={}",
                                userPrincipal.getId(), userPrincipal.getEmail());
                        return userPrincipal.getId();
                    }
                }
            }

            // 2. principal 파라미터에서 추출 시도
            if (principal != null) {
                if (principal instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
                    Object principalObj = auth.getPrincipal();

                    if (principalObj instanceof UserPrincipal) {
                        UserPrincipal userPrincipal = (UserPrincipal) principalObj;
                        return userPrincipal.getId();
                    }
                } else if (principal instanceof UserPrincipal) {
                    return ((UserPrincipal) principal).getId();
                }
            }

            log.error("WS:AUTH:FAILED:::Could not extract member ID from principal");
            return null;

        } catch (Exception e) {
            log.error("WS:AUTH:EXTRACTION_ERROR:::error={}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Principal과 HeaderAccessor에서 Member Name을 추출하는 헬퍼 메서드
     */
    private String extractMemberName(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        try {
            // 1. headerAccessor의 User에서 추출 시도
            if (headerAccessor != null && headerAccessor.getUser() != null) {
                Object user = headerAccessor.getUser();

                if (user instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) user;
                    Object principalObj = auth.getPrincipal();

                    if (principalObj instanceof UserPrincipal) {
                        return ((UserPrincipal) principalObj).getMemberName();
                    }
                }
            }

            // 2. principal 파라미터에서 추출 시도
            if (principal != null) {
                if (principal instanceof UsernamePasswordAuthenticationToken) {
                    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
                    Object principalObj = auth.getPrincipal();

                    if (principalObj instanceof UserPrincipal) {
                        return ((UserPrincipal) principalObj).getMemberName();
                    }
                } else if (principal instanceof UserPrincipal) {
                    return ((UserPrincipal) principal).getMemberName();
                }
            }

            return "Unknown";

        } catch (Exception e) {
            log.error("WS:NAME:EXTRACTION_ERROR:::error={}", e.getMessage());
            return "Unknown";
        }
    }

    // 타이핑 알림용 DTO
    @lombok.Getter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TypingNotification {
        private Long chatRoomId;
        private boolean typing;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class TypingResponse {
        private Long userId;
        private String userName;
        private boolean typing;
    }
}