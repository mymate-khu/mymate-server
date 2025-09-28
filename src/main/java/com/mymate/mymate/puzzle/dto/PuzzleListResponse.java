package com.mymate.mymate.puzzle.dto;

import com.mymate.mymate.puzzle.entity.Puzzle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleListResponse {

    private List<PuzzleResponse> puzzles;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;
    private boolean first;
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
