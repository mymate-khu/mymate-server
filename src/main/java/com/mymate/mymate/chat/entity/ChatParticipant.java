package com.mymate.mymate.chat.entity;

import com.mymate.mymate.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "member_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }

    public void leave() {
        this.isActive = false;
    }
}