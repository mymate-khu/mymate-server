package com.mymate.mymate.rulebook.service;

import com.mymate.mymate.common.exception.general.GeneralException;
import com.mymate.mymate.common.exception.group.GroupHandler;
import com.mymate.mymate.common.exception.rulebook.RulebookHandler;
import com.mymate.mymate.common.exception.rulebook.status.RulebookErrorStatus;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.rulebook.dto.RulebookCreateRequest;
import com.mymate.mymate.rulebook.dto.RulebookResponse;
import com.mymate.mymate.rulebook.dto.RulebookUpdateRequest;
import com.mymate.mymate.rulebook.entity.Rulebook;
import com.mymate.mymate.rulebook.repository.RulebookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RulebookServiceImpl implements RulebookService {

    private final RulebookRepository rulebookRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public RulebookResponse createRulebook(Long memberId, RulebookCreateRequest request) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 룰북 생성
        Rulebook rulebook = Rulebook.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .groupId(groupId)
                .createdBy(memberId)
                .build();

        Rulebook savedRulebook = rulebookRepository.save(rulebook);
        log.info("룰북 생성 완료: rulebookId={}, groupId={}, createdBy={}",
                savedRulebook.getId(), groupId, memberId);

        return RulebookResponse.from(savedRulebook);
    }

    @Override
    public RulebookResponse getRulebook(Long memberId, Long rulebookId) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 룰북 조회 (같은 그룹인지 확인)
        Rulebook rulebook = rulebookRepository.findByIdAndGroupId(rulebookId, groupId)
                .orElseThrow(() -> new RulebookHandler(RulebookErrorStatus.RULEBOOK_NOT_FOUND));

        return RulebookResponse.from(rulebook);
    }

    @Override
    public List<RulebookResponse> getRulebooks(Long memberId) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 그룹의 모든 룰북 조회
        List<Rulebook> rulebooks = rulebookRepository.findByGroupIdOrderByCreatedAtDesc(groupId);

        return rulebooks.stream()
                .map(RulebookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RulebookResponse updateRulebook(Long memberId, Long rulebookId, RulebookUpdateRequest request) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 룰북 조회 (같은 그룹인지 확인)
        Rulebook rulebook = rulebookRepository.findByIdAndGroupId(rulebookId, groupId)
                .orElseThrow(() -> new RulebookHandler(RulebookErrorStatus.RULEBOOK_NOT_FOUND));

        // 수정할 필드들 업데이트
        if (request.getTitle() != null) {
            rulebook.updateTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            rulebook.updateContent(request.getContent());
        }

        Rulebook savedRulebook = rulebookRepository.save(rulebook);
        log.info("룰북 수정 완료: rulebookId={}, groupId={}, updatedBy={}",
                rulebookId, groupId, memberId);

        return RulebookResponse.from(savedRulebook);
    }

    @Override
    @Transactional
    public void deleteRulebook(Long memberId, Long rulebookId) {
        // 사용자의 그룹 조회
        Long groupId = getUserGroupId(memberId);

        // 룰북 조회 (같은 그룹인지 확인)
        Rulebook rulebook = rulebookRepository.findByIdAndGroupId(rulebookId, groupId)
                .orElseThrow(() -> new RulebookHandler(RulebookErrorStatus.RULEBOOK_NOT_FOUND));

        rulebookRepository.delete(rulebook);
        log.info("룰북 삭제 완료: rulebookId={}, groupId={}, deletedBy={}",
                rulebookId, groupId, memberId);
    }

    /**
     * 사용자의 그룹 ID를 조회하는 헬퍼 메서드
     */
    private Long getUserGroupId(Long memberId) {
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        if (groupMembers.isEmpty()) {
            throw new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND);
        }

        // 사용자당 하나의 그룹만 가질 수 있으므로 첫 번째 그룹 반환
        return groupMembers.get(0).getGroupId();
    }
}