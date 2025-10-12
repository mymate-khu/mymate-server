package com.mymate.mymate.mateboard.service;

import com.mymate.mymate.mateboard.dto.MateBoardCreateRequest;
import com.mymate.mymate.mateboard.dto.MateBoardListResponse;
import com.mymate.mymate.mateboard.dto.MateBoardResponse;
import com.mymate.mymate.mateboard.dto.MateBoardUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface MateBoardService {

    MateBoardResponse createMateBoard(Long memberId, MateBoardCreateRequest request);

    MateBoardResponse getMateBoard(Long memberId, Long mateBoardId);

    MateBoardListResponse getMateBoards(Long memberId, Pageable pageable);

    MateBoardResponse updateMateBoard(Long memberId, Long mateBoardId, MateBoardUpdateRequest request);

    void deleteMateBoard(Long memberId, Long mateBoardId);

    void cleanupExpiredMateBoards();
}