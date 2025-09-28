package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.entity.Puzzle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "퍼즐 목록 응답", example = """
{
  "puzzles": [
    {
      "id": 1,
      "title": "운동하기",
      "description": "매일 30분 운동하기",
      "scheduledDate": "2024-01-15",
      "completedAt": null,
      "status": "PENDING",
      "memberId": 123,
      "recurrenceType": "DAILY",
      "recurrenceEndDate": "2024-01-31",
      "parentPuzzleId": null,
      "priority": "HIGH",
      "category": "건강",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "size": 10,
  "first": true,
  "last": true
}
""")
public class PuzzleListResponse {

    @Schema(description = "퍼즐 목록")
    private List<PuzzleResponse> puzzles;
    
    @Schema(description = "전체 요소 수", example = "1")
    private long totalElements;
    
    @Schema(description = "전체 페이지 수", example = "1")
    private int totalPages;
    
    @Schema(description = "현재 페이지", example = "0")
    private int currentPage;
    
    @Schema(description = "페이지 크기", example = "10")
    private int size;
    
    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean first;
    
    @Schema(description = "마지막 페이지 여부", example = "true")
    private boolean last;

    public static PuzzleListResponse from(List<Puzzle> puzzles, long totalElements, int totalPages, 
                                        int currentPage, int size, boolean first, boolean last) {
        List<PuzzleResponse> puzzleResponses = puzzles.stream()
                .map(PuzzleResponse::from)
                .toList();
        
        return PuzzleListResponse.builder()
                .puzzles(puzzleResponses)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .size(size)
                .first(first)
                .last(last)
                .build();
    }
}
