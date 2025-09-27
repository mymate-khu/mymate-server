package com.mymate.mymate.member.service;

import com.mymate.mymate.member.dto.ProfileSummaryResponse;
import com.mymate.mymate.member.dto.ProfileUpdateRequest;

public interface MemberService {
    ProfileSummaryResponse getMyProfile(Long memberId);
    ProfileSummaryResponse updateMyProfile(Long memberId, ProfileUpdateRequest request);
}
