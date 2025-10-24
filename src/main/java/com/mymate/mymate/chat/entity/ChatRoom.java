package com.mymate.mymate.chat.entity;

import com.mymate.mymate.common.entity.BaseEntity;
import com.mymate.mymate.chat.enums.ChatRoomType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity {

    @Id
    private Long id; // groupId와 동일한 값 사용

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRoomType type;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ChatRoom 생성 시 id와 groupId를 동일하게 설정하는 생성자
    public static ChatRoom createForGroup(Long groupId, String name, ChatRoomType type) {
        return ChatRoom.builder()
                .id(groupId)
                .groupId(groupId)
                .name(name)
                .type(type)
                .isActive(true)
                .build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.isActive = false;
    }
}