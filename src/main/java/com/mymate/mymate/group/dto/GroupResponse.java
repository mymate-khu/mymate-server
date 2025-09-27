package com.mymate.mymate.group.dto;

import com.mymate.mymate.group.entity.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class GroupResponse {

    private Long id;
    private String name;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MemberResponse> members;

    public GroupResponse(Group group, List<MemberResponse> members) {
        this.id = group.getId();
        this.name = group.getName();
        this.ownerId = group.getOwnerId();
        this.createdAt = group.getCreatedAt();
        this.updatedAt = group.getUpdatedAt();
        this.members = members;
    }

    @Getter
    @NoArgsConstructor
    public static class MemberResponse {
        private Long id;
        private Long memberId;
        private LocalDateTime joinedAt;

        public MemberResponse(Long id, Long memberId, LocalDateTime joinedAt) {
            this.id = id;
            this.memberId = memberId;
            this.joinedAt = joinedAt;
        }
    }
}
