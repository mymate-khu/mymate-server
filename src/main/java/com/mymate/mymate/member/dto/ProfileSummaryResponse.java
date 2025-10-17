package com.mymate.mymate.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로필 요약 응답", example = """
{
  "id": 123,
  "memberLoginId": "sw1234",
  "username": "홍길동",
  "email": "user@example.com",
  "nickname": "길동이",
  "profileImageUrl": "https://example.com/profile.jpg",
  "bio": "안녕하세요! 홍길동입니다.",
  "signUpCompleted": true
}
""")
public class ProfileSummaryResponse {

    @Schema(description = "ID", example = "123")
    private Long id;

    @Schema(description = "로그인 아이디", example = "sw1234")
    private String memberLoginId;
    
    @Schema(description = "사용자명", example = "홍길동")
    private String username;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "길동이")
    private String nickname;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
    
    @Schema(description = "자기소개", example = "안녕하세요! 홍길동입니다.")
    private String bio;

    @Schema(description = "회원가입 완료 여부", example = "true")
    private boolean signUpCompleted;
}



