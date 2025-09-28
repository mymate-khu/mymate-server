package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "퍼즐 수정 요청", example = """
{
  "title": "운동하기 (수정)",
  "description": "매일 1시간 운동하기",
  "scheduledDate": "2024-01-16",
  "priority": "MEDIUM",
  "category": "건강관리"
}
""")
public class PuzzleUpdateRequest {

    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    private String description;

    private LocalDate scheduledDate;

    private Priority priority;

    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    private String category;
}
