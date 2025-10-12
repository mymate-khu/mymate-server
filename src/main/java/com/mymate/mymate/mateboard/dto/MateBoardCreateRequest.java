package com.mymate.mymate.mateboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "메이트보드 생성 요청", example = """
{
  "content": "오늘 하루도 화이팅! 모두 건강하게 보내세요 😊"
}
""")
public class MateBoardCreateRequest {

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 1000, message = "내용은 1000자를 초과할 수 없습니다")
    @Schema(description = "메이트보드 내용", example = "오늘 하루도 화이팅! 모두 건강하게 보내세요 😊")
    private String content;

    public MateBoardCreateRequest(String content) {
        this.content = content;
    }
}
