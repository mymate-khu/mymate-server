package com.mymate.mymate.member.service;

import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.association.MemberProfile;
import com.mymate.mymate.member.dto.ProfileSummaryResponse;
import com.mymate.mymate.member.dto.ProfileUpdateRequest;
import com.mymate.mymate.member.repository.MemberProfileRepository;
import com.mymate.mymate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileSummaryResponse getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MemberErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        MemberProfile profile = memberProfileRepository.findByMemberId(member.getId()).orElse(null);

        return ProfileSummaryResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(profile != null ? profile.getNickname() : null)
                .profileImageUrl(profile != null ? profile.getProfileImageUrl() : null)
                .bio(profile != null ? profile.getBio() : null)
                .signUpCompleted(member.isSignUpCompleted())
                .build();
    }

    @Override
    @Transactional
    public ProfileSummaryResponse updateMyProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MemberErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        MemberProfile profile = memberProfileRepository.findByMemberId(member.getId())
                .orElseGet(() -> MemberProfile.builder().memberId(member.getId()).build());

        if (request.getNickname() != null) {
            profile.setNickname(request.getNickname());
        }
        if (request.getProfileImageUrl() != null) {
            profile.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }

        memberProfileRepository.save(profile);

        return ProfileSummaryResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(profile.getNickname())
                .profileImageUrl(profile.getProfileImageUrl())
                .bio(profile.getBio())
                .signUpCompleted(member.isSignUpCompleted())
                .build();
    }
}
