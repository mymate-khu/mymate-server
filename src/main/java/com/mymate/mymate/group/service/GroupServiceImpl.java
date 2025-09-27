package com.mymate.mymate.group.service;

import com.mymate.mymate.group.dto.GroupCreateRequest;
import com.mymate.mymate.group.dto.GroupResponse;
import com.mymate.mymate.group.dto.GroupUpdateNameRequest;
import com.mymate.mymate.group.entity.Group;
import com.mymate.mymate.group.entity.GroupMember;
import com.mymate.mymate.group.repository.GroupMemberRepository;
import com.mymate.mymate.group.repository.GroupRepository;
import com.mymate.mymate.member.repository.MemberRepository;
import com.mymate.mymate.common.exception.group.GroupHandler;
import com.mymate.mymate.group.status.GroupErrorStatus;
import com.mymate.mymate.common.exception.member.MemberHandler;
import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public GroupResponse createGroup(GroupCreateRequest request, Long ownerId) {
        // 소유자 정보 확인
        memberRepository.findById(ownerId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        // 기본 그룹명 설정 (요청이 없으면 "우리집")
        String groupName = request.getName() != null ? request.getName() : "우리집";

        // 그룹 생성
        Group group = Group.builder()
                .name(groupName)
                .ownerId(ownerId)
                .build();
        
        Group savedGroup = groupRepository.save(group);

        // 소유자를 그룹 멤버로 추가
        GroupMember ownerMember = GroupMember.builder()
                .groupId(savedGroup.getId())
                .memberId(ownerId)
                .build();
        
        groupMemberRepository.save(ownerMember);

        log.info("그룹 생성 완료: groupId={}, ownerId={}, name={}", savedGroup.getId(), ownerId, groupName);

        return new GroupResponse(savedGroup, List.of());
    }

    @Override
    public List<GroupResponse> getMyGroups(Long memberId) {
        // 멤버가 속한 그룹들 조회
        List<GroupMember> groupMembers = groupMemberRepository.findByMemberId(memberId);
        
        return groupMembers.stream()
                .map(groupMember -> {
                    Group group = groupRepository.findById(groupMember.getGroupId())
                            .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));
                    
                    // 그룹의 모든 멤버 조회
                    List<GroupMember> allMembers = groupMemberRepository.findByGroupId(group.getId());
                    List<GroupResponse.MemberResponse> memberResponses = allMembers.stream()
                            .map(member -> new GroupResponse.MemberResponse(
                                    member.getId(), 
                                    member.getMemberId(), 
                                    member.getJoinedAt()))
                            .collect(Collectors.toList());
                    
                    return new GroupResponse(group, memberResponses);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));

        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.MEMBER_NOT_IN_GROUP));

        // 소유자가 마지막 멤버인 경우 그룹 삭제
        if (group.getOwnerId().equals(memberId)) {
            List<GroupMember> remainingMembers = groupMemberRepository.findByGroupId(groupId);
            if (remainingMembers.size() == 1) {
                groupRepository.delete(group);
                log.info("그룹 삭제됨 (소유자 탈퇴): groupId={}", groupId);
                return;
            }
        }

        // 그룹 멤버에서 제거
        groupMemberRepository.delete(groupMember);
        log.info("그룹 탈퇴 완료: groupId={}, memberId={}", groupId, memberId);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long memberId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));

        // 소유자만 멤버 제거 가능
        if (!group.getOwnerId().equals(requesterId)) {
            throw new GroupHandler(GroupErrorStatus.FORBIDDEN);
        }

        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.MEMBER_NOT_IN_GROUP));

        // 소유자 본인은 제거할 수 없음
        if (groupMember.getMemberId().equals(group.getOwnerId())) {
            throw new GroupHandler(GroupErrorStatus.CANNOT_REMOVE_OWNER);
        }

        groupMemberRepository.delete(groupMember);
        log.info("그룹 멤버 제거 완료: groupId={}, removedMemberId={}, requesterId={}", 
                groupId, memberId, requesterId);
    }

    @Override
    @Transactional
    public void addMember(Long groupId, Long memberId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));

        // 소유자만 멤버 추가 가능
        if (!group.getOwnerId().equals(requesterId)) {
            throw new GroupHandler(GroupErrorStatus.FORBIDDEN);
        }

        // 이미 그룹에 속한 멤버인지 확인
        if (groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new GroupHandler(GroupErrorStatus.MEMBER_ALREADY_IN_GROUP);
        }

        // 기존 그룹에서 탈퇴 처리 (단일 그룹 정책)
        List<GroupMember> existingMemberships = groupMemberRepository.findByMemberId(memberId);
        for (GroupMember existingMembership : existingMemberships) {
            groupMemberRepository.delete(existingMembership);
        }

        // 새 그룹에 멤버 추가
        GroupMember newMember = GroupMember.builder()
                .groupId(groupId)
                .memberId(memberId)
                .build();
        
        groupMemberRepository.save(newMember);
        log.info("그룹 멤버 추가 완료: groupId={}, memberId={}, requesterId={}", 
                groupId, memberId, requesterId);
    }

    @Override
    @Transactional
    public GroupResponse updateGroupName(Long groupId, GroupUpdateNameRequest request, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupHandler(GroupErrorStatus.GROUP_NOT_FOUND));

        // 소유자만 그룹 이름 변경 가능
        if (!group.getOwnerId().equals(requesterId)) {
            throw new GroupHandler(GroupErrorStatus.FORBIDDEN);
        }

        // 그룹 이름 업데이트
        group.updateName(request.getName());
        Group savedGroup = groupRepository.save(group);

        // 그룹의 모든 멤버 조회
        List<GroupMember> allMembers = groupMemberRepository.findByGroupId(groupId);
        List<GroupResponse.MemberResponse> memberResponses = allMembers.stream()
                .map(member -> new GroupResponse.MemberResponse(
                        member.getId(), 
                        member.getMemberId(), 
                        member.getJoinedAt()))
                .collect(Collectors.toList());

        log.info("그룹 이름 변경 완료: groupId={}, newName={}, requesterId={}", 
                groupId, request.getName(), requesterId);

        return new GroupResponse(savedGroup, memberResponses);
    }
}
