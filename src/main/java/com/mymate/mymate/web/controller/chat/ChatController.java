package com.mymate.mymate.web.controller.chat;

import com.mymate.mymate.auth.jwt.UserPrincipal;
import com.mymate.mymate.chat.dto.*;
import com.mymate.mymate.chat.service.ChatService;
import com.mymate.mymate.common.exception.ApiErrorCodeExample;
import com.mymate.mymate.common.exception.ApiResponse;
import com.mymate.mymate.common.exception.general.status.ErrorStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "실시간 채팅 API")
@SecurityRequirement(name = "accessToken")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    @Operation(
            summary = "채팅방 생성",
            description = "새로운 채팅방을 생성합니다. 그룹의 모든 멤버가 자동으로 참여자로 추가됩니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"FORBIDDEN", "BAD_REQUEST"})
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(
            @Valid @RequestBody ChatRoomCreateRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ChatRoomResponse response = chatService.createChatRoom(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "CHAT2001", "채팅방이 생성되었습니다.", response));
    }

    @GetMapping("/rooms")
    @Operation(
            summary = "내 채팅방 목록 조회",
            description = "현재 사용자가 참여중인 모든 채팅방 목록을 조회합니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"UNAUTHORIZED"})
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyChatRooms(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<ChatRoomResponse> response = chatService.getMyChatRooms(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "CHAT2002", "채팅방 목록을 조회했습니다.", response));
    }

    @GetMapping("/rooms/{chatRoomId}")
    @Operation(
            summary = "채팅방 상세 조회",
            description = "특정 채팅방의 상세 정보를 조회합니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"NOT_FOUND", "FORBIDDEN"})
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getChatRoom(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long chatRoomId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ChatRoomResponse response = chatService.getChatRoom(userPrincipal.getId(), chatRoomId);
        return ResponseEntity.ok(new ApiResponse<>(true, "CHAT2003", "채팅방을 조회했습니다.", response));
    }

    @PostMapping("/rooms/{chatRoomId}/leave")
    @Operation(
            summary = "채팅방 나가기",
            description = "채팅방에서 나갑니다. 나간 후에는 해당 채팅방의 메시지를 볼 수 없습니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"NOT_FOUND", "FORBIDDEN"})
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long chatRoomId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        chatService.leaveChatRoom(userPrincipal.getId(), chatRoomId);
        return ResponseEntity.ok(new ApiResponse<>(true, "CHAT2004", "채팅방에서 나갔습니다.", null));
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    @Operation(
            summary = "채팅 메시지 목록 조회",
            description = "채팅방의 메시지 목록을 페이징하여 조회합니다. 최신 메시지부터 조회됩니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"NOT_FOUND", "FORBIDDEN"})
    public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> getMessages(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long chatRoomId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "50")
            @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatMessageResponse> response = chatService.getMessages(userPrincipal.getId(), chatRoomId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "CHAT2005", "메시지 목록을 조회했습니다.", response));
    }

    @PostMapping("/messages")
    @Operation(
            summary = "채팅 메시지 전송 (REST)",
            description = "REST API를 통해 채팅 메시지를 전송합니다. 실시간 전송은 WebSocket을 사용하세요.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"FORBIDDEN", "BAD_REQUEST"})
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ChatMessageResponse response = chatService.sendMessage(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "CHAT2006", "메시지가 전송되었습니다.", response));
    }

    @DeleteMapping("/messages/{messageId}")
    @Operation(
            summary = "채팅 메시지 삭제",
            description = "본인이 작성한 메시지를 삭제합니다. 삭제된 메시지는 '삭제된 메시지입니다'로 표시됩니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"NOT_FOUND", "FORBIDDEN"})
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @Parameter(description = "메시지 ID", required = true)
            @PathVariable Long messageId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        chatService.deleteMessage(userPrincipal.getId(), messageId);
        return ResponseEntity.ok(new ApiResponse<>(true, "CHAT2007", "메시지가 삭제되었습니다.", null));
    }

    @PostMapping("/rooms/{chatRoomId}/read")
    @Operation(
            summary = "메시지 읽음 처리",
            description = "채팅방의 메시지를 읽음 처리합니다. 읽지 않은 메시지 카운트가 초기화됩니다.",
            tags = {"Chat"}
    )
    @ApiErrorCodeExample(value = ErrorStatus.class, codes = {"NOT_FOUND", "FORBIDDEN"})
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long chatRoomId,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        chatService.markAsRead(userPrincipal.getId(), chatRoomId);
        return ResponseEntity.ok(new ApiResponse<>(true, "CHAT2008", "메시지를 읽음 처리했습니다.", null));
    }
}