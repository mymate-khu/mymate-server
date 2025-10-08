package com.mymate.mymate.rulebook.service;

import com.mymate.mymate.rulebook.dto.RulebookCreateRequest;
import com.mymate.mymate.rulebook.dto.RulebookResponse;
import com.mymate.mymate.rulebook.dto.RulebookUpdateRequest;

import java.util.List;

public interface RulebookService {

    // 룰북 생성
    RulebookResponse createRulebook(Long memberId, RulebookCreateRequest request);

    // 룰북 상세 조회
    RulebookResponse getRulebook(Long memberId, Long rulebookId);

    // 그룹의 모든 룰북 조회
    List<RulebookResponse> getRulebooks(Long memberId);

    // 룰북 수정
    RulebookResponse updateRulebook(Long memberId, Long rulebookId, RulebookUpdateRequest request);

    // 룰북 삭제
    void deleteRulebook(Long memberId, Long rulebookId);
}