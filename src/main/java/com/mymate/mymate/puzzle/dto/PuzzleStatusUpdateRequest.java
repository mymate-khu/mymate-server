package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "퍼즐 상태 변경 요청", example = """
{
  "status": "DONE"
}
""")
public class PuzzleStatusUpdateRequest {

    @NotNull(message = "상태는 필수입니다")
    private PuzzleStatus status;
}
