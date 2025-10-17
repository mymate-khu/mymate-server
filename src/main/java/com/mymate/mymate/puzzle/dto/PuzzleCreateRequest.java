package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.enums.Priority;
import com.mymate.mymate.puzzle.enums.RecurrenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "퍼즐 생성 요청", example = """
{
  "title": "운동하기",
  "description": "매일 30분 운동하기",
  "scheduledDate": "2024-01-15",
  "memberLoginId": "sw1234",
  "recurrenceType": "DAILY",
  "recurrenceEndDate": "2024-01-31",
  "priority": "HIGH",
  "category": "건강"
}
""")
public class PuzzleCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    private String description;

    @NotNull(message = "예정일은 필수입니다")
    private LocalDate scheduledDate;

    @Schema(description = "생성자 로그인 아이디", example = "sw1234")
    private String memberLoginId;

    @Builder.Default
    private RecurrenceType recurrenceType = RecurrenceType.NONE;

    private LocalDate recurrenceEndDate;

    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    private String category;
}