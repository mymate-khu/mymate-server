package com.mymate.mymate.group.service;

import com.mymate.mymate.group.dto.GroupCreateRequest;
import com.mymate.mymate.group.dto.GroupResponse;

import java.util.List;

public interface GroupService {
    
    GroupResponse createGroup(GroupCreateRequest request, Long ownerId);
    
    List<GroupResponse> getMyGroups(Long memberId);
    
    void leaveGroup(Long groupId, Long memberId);
    
    void removeMember(Long groupId, Long memberId, Long requesterId);
    
    void addMember(Long groupId, Long memberId, Long requesterId);
}