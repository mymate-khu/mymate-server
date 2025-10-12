package com.mymate.mymate.mateboard.service;

import com.mymate.mymate.common.exception.group.GroupHandler;
import com.mymate.mymate.common.exception.mateboard.MateBoardHandler;
import com.mymate.mymate.common.exception.mateboard.status.MateBoardErrorStatus;
import com.mymate.mymate.common.exception.member.MemberHandler;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.mateboard.dto.MateBoardCreateRequest;
import com.mymate.mymate.mateboard.dto.MateBoardListResponse;
import com.mymate.mymate.mateboard.dto.MateBoardResponse;
import com.mymate.mymate.mateboard.dto.MateBoardUpdateRequest;
import com.mymate.mymate.mateboard.entity.MateBoard;
import com.mymate.mymate.mateboard.repository.MateBoardRepository;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MateBoardServiceImpl implements MateBoardService {

    private final MateBoardRepository mateBoardRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MateBoardResponse createMateBoard(Long memberId, MateBoardCreateRequest request) {
        // 사용자가 속한 그룹 조회
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        if (groupMembers.isEmpty()) {
            throw new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND);
        }

        Long groupId = groupMembers.get(0).getGroupId(); // 사용자는 하나의 그룹만 가질 수 있음

        // 멤버 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        // 메이트보드 생성
        MateBoard mateBoard = MateBoard.builder()
                .memberId(memberId)
                .groupId(groupId)
                .content(request.getContent())
                .build();

        MateBoard savedMateBoard = mateBoardRepository.save(mateBoard);

        String memberName = member.getUsername() != null ? member.getUsername() : member.getEmail();

        log.info("메이트보드 생성 완료: mateBoardId={}, memberId={}, groupId={}",
                savedMateBoard.getId(), memberId, groupId);

        return new MateBoardResponse(savedMateBoard, memberName, true);
    }

    @Override
    public MateBoardResponse getMateBoard(Long memberId, Long mateBoardId) {
        // 사용자가 속한 그룹 조회
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        if (groupMembers.isEmpty()) {
            throw new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND);
        }

        Long groupId = groupMembers.get(0).getGroupId();

        // 메이트보드 조회 (같은 그룹이고 만료되지 않은 것)
        MateBoard mateBoard = mateBoardRepository.findByIdAndGroupId(mateBoardId, groupId)
                .orElseThrow(() -> new MateBoardHandler(MateBoardErrorStatus.MATEBOARD_NOT_FOUND));

        if (mateBoard.isExpired()) {
            throw new MateBoardHandler(MateBoardErrorStatus.MATEBOARD_EXPIRED);
        }

        // 작성자 정보 조회
        Member member = memberRepository.findById(mateBoard.getMemberId())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        String memberName = member.getUsername() != null ? member.getUsername() : member.getEmail();
        boolean isOwner = mateBoard.getMemberId().equals(memberId);

        return new MateBoardResponse(mateBoard, memberName, isOwner);
    }

    @Override
    public MateBoardListResponse getMateBoards(Long memberId, Pageable pageable) {
        // 사용자가 속한 그룹 조회
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        if (groupMembers.isEmpty()) {
            throw new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND);
        }

        Long groupId = groupMembers.get(0).getGroupId();
        LocalDateTime now = LocalDateTime.now();

        // 그룹의 만료되지 않은 메이트보드 목록 조회
        Page<MateBoard> mateBoardPage = mateBoardRepository.findByGroupIdAndNotExpired(groupId, now, pageable);

        List<MateBoardResponse> mateBoardResponses = mateBoardPage.getContent().stream()
                .map(mateBoard -> {
                    Member member = memberRepository.findById(mateBoard.getMemberId())
                            .orElse(null);
                    String memberName = member != null ?
                            (member.getUsername() != null ? member.getUsername() : member.getEmail()) : "알 수 없음";
                    boolean isOwner = mateBoard.getMemberId().equals(memberId);
                    return new MateBoardResponse(mateBoard, memberName, isOwner);
                })
                .collect(Collectors.toList());

        return new MateBoardListResponse(
                mateBoardResponses,
                mateBoardPage.getTotalElements(),
                mateBoardPage.getNumber(),
                mateBoardPage.getSize(),
                mateBoardPage.getTotalPages()
        );
    }

    @Override
    @Transactional
    public MateBoardResponse updateMateBoard(Long memberId, Long mateBoardId, MateBoardUpdateRequest request) {
        // 메이트보드 조회 및 권한 확인
        MateBoard mateBoard = mateBoardRepository.findByIdAndMemberId(mateBoardId, memberId)
                .orElseThrow(() -> new MateBoardHandler(MateBoardErrorStatus.MATEBOARD_UPDATE_DENIED));

        if (mateBoard.isExpired()) {
            throw new MateBoardHandler(MateBoardErrorStatus.MATEBOARD_EXPIRED);
        }

        // 내용 수정
        mateBoard.updateContent(request.getContent());
        MateBoard updatedMateBoard = mateBoardRepository.save(mateBoard);

        // 작성자 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        String memberName = member.getUsername() != null ? member.getUsername() : member.getEmail();

        log.info("메이트보드 수정 완료: mateBoardId={}, memberId={}", mateBoardId, memberId);

        return new MateBoardResponse(updatedMateBoard, memberName, true);
    }

    @Override
    @Transactional
    public void deleteMateBoard(Long memberId, Long mateBoardId) {
        // 메이트보드 조회 및 권한 확인
        MateBoard mateBoard = mateBoardRepository.findByIdAndMemberId(mateBoardId, memberId)
                .orElseThrow(() -> new MateBoardHandler(MateBoardErrorStatus.MATEBOARD_DELETE_DENIED));

        mateBoardRepository.delete(mateBoard);

        log.info("메이트보드 삭제 완료: mateBoardId={}, memberId={}", mateBoardId, memberId);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @Transactional
    public void cleanupExpiredMateBoards() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = mateBoardRepository.deleteExpiredMateBoards(now);

        if (deletedCount > 0) {
            log.info("만료된 메이트보드 정리 완료: deletedCount={}", deletedCount);
        }
    }
}