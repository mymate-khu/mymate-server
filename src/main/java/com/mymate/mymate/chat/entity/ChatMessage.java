package com.mymate.mymate.chat.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import com.mymate.mymate.chat.enums.MessageType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_chat_room_created", columnList = "chatRoomId, createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
        this.content = "삭제된 메시지입니다.";
    }
}