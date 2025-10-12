package com.mymate.mymate.mateboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "메이트보드 수정 요청", example = """
{
  "content": "수정된 메시지입니다. 좋은 하루 되세요!"
}
""")
public class MateBoardUpdateRequest {

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 1000, message = "내용은 1000자를 초과할 수 없습니다")
    @Schema(description = "수정할 메이트보드 내용", example = "수정된 메시지입니다. 좋은 하루 되세요!")
    private String content;

    public MateBoardUpdateRequest(String content) {
        this.content = content;
    }
}