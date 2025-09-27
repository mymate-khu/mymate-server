package com.mymate.mymate.group.service;

import com.mymate.mymate.group.dto.InvitationCreateRequest;
import com.mymate.mymate.group.dto.InvitationResponse;

import java.util.List;

public interface InvitationService {
    
    InvitationResponse createInvitation(InvitationCreateRequest request, Long inviterId);
    
    List<InvitationResponse> getMyInvitations(Long memberId);
    
    void acceptInvitation(Long invitationId, Long memberId);
    
    void cancelInvitation(Long invitationId, Long memberId);
    
    void expireInvitations();
}