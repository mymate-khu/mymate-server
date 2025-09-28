package com.mymate.mymate.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "프로필 수정 요청", example = """
{
  "nickname": "홍길동",
  "profileImageUrl": "https://example.com/profile.jpg",
  "bio": "안녕하세요! 홍길동입니다."
}
""")
public class ProfileUpdateRequest {

    @Size(max = 40)
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Size(max = 200)
    @Schema(description = "자기소개", example = "안녕하세요! 홍길동입니다.")
    private String bio;
}



