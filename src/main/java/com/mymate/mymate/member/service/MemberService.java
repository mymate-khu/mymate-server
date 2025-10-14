package com.mymate.mymate.member.service;

import com.mymate.mymate.member.dto.MemberSearchResponse;
import com.mymate.mymate.member.dto.ProfileSummaryResponse;
import com.mymate.mymate.member.dto.ProfileUpdateRequest;

import java.util.List;

public interface MemberService {
    ProfileSummaryResponse getMyProfile(Long memberId);
    ProfileSummaryResponse updateMyProfile(Long memberId, ProfileUpdateRequest request);
    List<MemberSearchResponse> searchMembers(String query);
    List<MemberSearchResponse> getAllCompletedMembers();
}
