package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.enums.PuzzleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleStatusUpdateRequest {

    @NotNull(message = "상태는 필수입니다")
    private PuzzleStatus status;
}
