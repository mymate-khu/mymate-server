package com.mymate.mymate.mateboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "메이트보드 목록 응답")
public class MateBoardListResponse {

    @Schema(description = "메이트보드 목록")
    private List<MateBoardResponse> mateBoards;

    @Schema(description = "총 개수", example = "15")
    private long totalCount;

    @Schema(description = "페이지 번호", example = "0")
    private int page;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "총 페이지 수", example = "2")
    private int totalPages;

    public MateBoardListResponse(List<MateBoardResponse> mateBoards, long totalCount, int page, int size, int totalPages) {
        this.mateBoards = mateBoards;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
    }
}