package com.mymate.mymate.rulebook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "룰북 생성 요청", example = """
{
  "title": "우리집 생활 규칙",
  "content": "1. 설거지는 자기가 사용한 것은 자기가\\n2. 쓰레기는 분리배출하기\\n3. 공용 공간은 깔끔하게 정리하기"
}
""")
public class RulebookCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    @Schema(description = "룰북 제목", example = "우리집 생활 규칙")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 5000, message = "내용은 5000자를 초과할 수 없습니다")
    @Schema(description = "룰북 내용", example = "1. 설거지는 자기가 사용한 것은 자기가\\n2. 쓰레기는 분리배출하기\\n3. 공용 공간은 깔끔하게 정리하기")
    private String content;

    public RulebookCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}