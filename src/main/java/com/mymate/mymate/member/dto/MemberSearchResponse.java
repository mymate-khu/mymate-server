package com.mymate.mymate.member.dto;

import com.mymate.mymate.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "멤버 검색 응답", example = """
{
  "id": 123,
  "userId": "user123",
  "username": "홍길동",
  "email": "user@example.com"
}
""")
public class MemberSearchResponse {
    @Schema(description = "멤버 ID", example = "123")
    private Long id;
    
    @Schema(description = "사용자 ID", example = "user123")
    private String userId;
    
    @Schema(description = "사용자명", example = "홍길동")
    private String username;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    public MemberSearchResponse(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId();
        this.username = member.getUsername();
        this.email = member.getEmail();
    }
}