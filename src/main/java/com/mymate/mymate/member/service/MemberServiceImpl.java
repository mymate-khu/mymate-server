package com.mymate.mymate.member.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymate.mymate.common.exception.member.status.MemberErrorStatus;
import com.mymate.mymate.member.Member;
import com.mymate.mymate.member.association.MemberProfile;
import com.mymate.mymate.member.dto.MemberSearchResponse;
import com.mymate.mymate.member.dto.ProfileSummaryResponse;
import com.mymate.mymate.member.dto.ProfileUpdateRequest;
import com.mymate.mymate.member.repository.MemberProfileRepository;
import com.mymate.mymate.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

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
            String newNickname = request.getNickname();
            if (!Objects.equals(profile.getNickname(), newNickname)
                    && memberProfileRepository.existsByNickname(newNickname)) {
                throw new IllegalArgumentException("Nickname already in use");
            }
            profile.setNickname(newNickname);
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

    @Override
    @Transactional(readOnly = true)
    public List<MemberSearchResponse> searchMembers(String query) {
        List<Member> members = memberRepository.findByUsernameOrNameContainingIgnoreCase(query);
        return members.stream()
                .map(MemberSearchResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberSearchResponse> getAllCompletedMembers() {
        List<Member> members = memberRepository.findAllCompletedMembers();
        return members.stream()
                .map(MemberSearchResponse::new)
                .collect(Collectors.toList());
    }
}
