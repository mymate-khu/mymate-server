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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request,
                            @AuthenticationPrincipal UserPrincipal principal) {
        try {
            log.info("WS:MESSAGE:RECEIVED:::chatRoomId={}, senderId={}",
                    request.getChatRoomId(), principal.getId());

            ChatMessageResponse response = chatService.sendMessage(principal.getId(), request);

            // 채팅방의 모든 참여자에게 메시지 전송
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + request.getChatRoomId(),
                    response
            );

        } catch (Exception e) {
            log.error("WS:MESSAGE:ERROR:::error={}", e.getMessage(), e);

            // 사용자 ID를 문자열로 변환하여 전송
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(principal.getId()),
                    "/queue/errors",
                    "메시지 전송에 실패했습니다: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingNotification notification,
                             @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("WS:TYPING:::chatRoomId={}, userId={}, isTyping={}",
                notification.getChatRoomId(), principal.getId(), notification.isTyping());

        // 타이핑 상태를 다른 참여자들에게 전송 (자신 제외)
        messagingTemplate.convertAndSend(
                "/topic/chat/" + notification.getChatRoomId() + "/typing",
                new TypingResponse(principal.getId(), principal.getMemberName(), notification.isTyping())
        );
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