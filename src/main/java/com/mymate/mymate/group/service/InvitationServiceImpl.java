package com.mymate.mymate.group.service;

import com.mymate.mymate.group.dto.InvitationCreateRequest;
import com.mymate.mymate.group.dto.InvitationResponse;
import com.mymate.mymate.group.entity.Group;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.group.entity.Invitation;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.group.repository.GroupRepository;
import com.mymate.mymate.group.repository.InvitationRepository;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.repository.MemberRepository;
import com.mymate.mymate.common.exception.group.GroupHandler;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.common.exception.member.MemberHandler;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final GroupService groupService;

    private static final int INVITATION_EXPIRY_HOURS = 24; // 24시간 후 만료

    @Override
    @Transactional
    public List<InvitationResponse> createInvitations(InvitationCreateRequest request, String inviterMemberLoginId) {
        // memberLoginId로 초대자 Member 조회
        Member inviter = memberRepository.findByUserId(inviterMemberLoginId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        
        Long inviterId = inviter.getId();
        
        // 초대자의 그룹 조회 (사용자당 하나의 그룹만 가질 수 있음)
        List<GroupMember> inviterMemberships = groupMemberRepository.findByMemberId(inviterId);
        if (inviterMemberships.isEmpty()) {
            throw new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND);
        }
        
        Group group = groupRepository.findById(inviterMemberships.get(0).getGroupId())
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));

        // 소유자만 초대 가능
        if (!group.getOwnerId().equals(inviterId)) {
            throw new GroupHandler(GroupErrorStatus.FORBIDDEN);
        }

        // 초대자 이름 조회 (username이 null이면 email 사용)
        String inviterDisplayName = inviter.getUsername() != null ? inviter.getUsername() : inviter.getEmail();

        List<InvitationResponse> responses = new ArrayList<>();
        List<String> failedInvitations = new ArrayList<>();

        for (String inviteeIdentifier : request.getInviteeIdentifiers()) {
            try {
                // 초대받을 사용자 존재 확인 (userId 또는 email로 검색)
                Member invitee = memberRepository.findFirstByUserId(inviteeIdentifier)
                        .or(() -> memberRepository.findByEmail(inviteeIdentifier))
                        .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

                // 이미 그룹에 속한 멤버인지 확인
                if (groupMemberRepository.existsByGroupIdAndMemberId(group.getId(), invitee.getId())) {
                    failedInvitations.add(inviteeIdentifier + " (이미 그룹에 속한 멤버)");
                    continue;
                }

                // 이미 대기 중인 초대가 있는지 확인
                if (invitationRepository.existsByGroupIdAndInviteeIdAndStatus(
                        group.getId(), invitee.getId(), Invitation.InvitationStatus.PENDING)) {
                    failedInvitations.add(inviteeIdentifier + " (이미 대기 중인 초대)");
                    continue;
                }

                // 초대 생성
                Invitation invitation = Invitation.builder()
                        .groupId(group.getId())
                        .inviterId(inviterId)
                        .inviteeId(invitee.getId())
                        .expiresAt(LocalDateTime.now().plusHours(INVITATION_EXPIRY_HOURS))
                        .build();

                Invitation savedInvitation = invitationRepository.save(invitation);
                responses.add(new InvitationResponse(savedInvitation, group.getName(), inviterDisplayName));

                log.info("초대 생성 완료: invitationId={}, groupId={}, inviterId={}, inviteeId={}", 
                        savedInvitation.getId(), group.getId(), inviterId, invitee.getId());

            } catch (Exception e) {
                log.warn("초대 생성 실패: inviteeIdentifier={}, error={}", inviteeIdentifier, e.getMessage());
                failedInvitations.add(inviteeIdentifier + " (사용자를 찾을 수 없음)");
            }
        }

        if (!failedInvitations.isEmpty()) {
            log.warn("일부 초대 생성 실패: {}", String.join(", ", failedInvitations));
        }

        log.info("초대 생성 완료: 성공={}, 실패={}", responses.size(), failedInvitations.size());
        return responses;
    }

    @Override
    public List<InvitationResponse> getMyInvitations(Long memberId) {
        List<Invitation> invitations = invitationRepository.findPendingInvitationsByInviteeId(memberId);
        
        return invitations.stream()
                .map(invitation -> {
                    Group group = groupRepository.findById(invitation.getGroupId())
                            .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));
                    
                    Member inviter = memberRepository.findById(invitation.getInviterId())
                            .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
                    
                    String inviterDisplayName = inviter.getUsername() != null ? inviter.getUsername() : inviter.getEmail();
                    return new InvitationResponse(invitation, group.getName(), inviterDisplayName);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptInvitation(Long invitationId, Long memberId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.INVITATION_NOT_FOUND));

        // 초대받은 사용자만 수락 가능
        if (!invitation.getInviteeId().equals(memberId)) {
            throw new GroupHandler(GroupErrorStatus.FORBIDDEN);
        }

        // 이미 처리된 초대인지 확인
        if (!invitation.isPending()) {
            throw new GroupHandler(GroupErrorStatus.INVITATION_ALREADY_PROCESSED);
        }

        // 만료된 초대인지 확인
        if (invitation.isExpired()) {
            invitation.expire();
            invitationRepository.save(invitation);
            throw new GroupHandler(GroupErrorStatus.INVITATION_EXPIRED);
        }

        // 그룹에 멤버 추가 (단일 그룹 정책으로 기존 그룹에서 자동 탈퇴)
        groupService.addMember(invitation.getGroupId(), memberId, invitation.getInviterId());

        // 초대 상태를 수락으로 변경
        invitation.accept();
        invitationRepository.save(invitation);

        log.info("초대 수락 완료: invitationId={}, groupId={}, memberId={}", 
                invitationId, invitation.getGroupId(), memberId);
    }

    @Override
    @Transactional
    public void cancelInvitation(Long invitationId, Long memberId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.INVITATION_NOT_FOUND));

        // 초대받은 사용자 또는 초대한 사용자만 취소 가능
        if (!invitation.getInviteeId().equals(memberId) && 
            !invitation.getInviterId().equals(memberId)) {
            throw new GroupHandler(GroupErrorStatus.FORBIDDEN);
        }

        // 이미 처리된 초대인지 확인
        if (!invitation.isPending()) {
            throw new GroupHandler(GroupErrorStatus.INVITATION_ALREADY_PROCESSED);
        }

        invitation.cancel();
        invitationRepository.save(invitation);

        log.info("초대 취소 완료: invitationId={}, memberId={}", invitationId, memberId);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @Transactional
    public void expireInvitations() {
        List<Invitation> expiredInvitations = invitationRepository.findExpiredInvitations(LocalDateTime.now());
        
        for (Invitation invitation : expiredInvitations) {
            invitation.expire();
            invitationRepository.save(invitation);
        }

        if (!expiredInvitations.isEmpty()) {
            log.info("만료된 초대 처리 완료: count={}", expiredInvitations.size());
        }
    }
}
