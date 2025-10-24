package com.mymate.mymate.group.dto;

import com.mymate.mymate.group.entity.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "그룹 응답", example = """
{
  "id": 1,
  "name": "우리 가족",
  "ownerId": 123,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "members": [
    {
      "memberLoginId": "SZZYDE770",
      "username": "홍길동",
      "joinedAt": "2024-01-15T10:30:00"
    }
  ]
}
""")
public class GroupResponse {

    @Schema(description = "그룹 ID", example = "1")
    private Long id;
    
    @Schema(description = "그룹명", example = "우리 가족")
    private String name;
    
    @Schema(description = "그룹장 ID", example = "123")
    private Long ownerId;
    
    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "그룹 멤버 목록")
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
    @Schema(description = "그룹 멤버 정보")
    public static class MemberResponse {
        @Schema(description = "멤버 로그인 ID", example = "SZZYDE770")
        private String memberLoginId;
        
        @Schema(description = "사용자명", example = "홍길동")
        private String username;
        
        @Schema(description = "가입일시", example = "2024-01-15T10:30:00")
        private LocalDateTime joinedAt;

        public MemberResponse(String memberLoginId, String username, LocalDateTime joinedAt) {
            this.memberLoginId = memberLoginId;
            this.username = username;
            this.joinedAt = joinedAt;
        }
    }
}
