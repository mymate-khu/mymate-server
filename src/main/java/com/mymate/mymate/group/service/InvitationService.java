package com.mymate.mymate.group.service;

import com.mymate.mymate.group.dto.InvitationCreateRequest;
import com.mymate.mymate.group.dto.InvitationResponse;

import java.util.List;

public interface InvitationService {
    
    List<InvitationResponse> createInvitations(InvitationCreateRequest request, String inviterMemberLoginId);
    
    List<InvitationResponse> getMyInvitations(Long memberId);
    
    List<InvitationResponse> getSentInvitations(Long memberId);
    
    List<InvitationResponse> getSentInvitations(Long memberId, String status);
    
    void acceptInvitation(Long invitationId, Long memberId);
    
    void cancelInvitation(Long invitationId, Long memberId);
    
    void expireInvitations();
}