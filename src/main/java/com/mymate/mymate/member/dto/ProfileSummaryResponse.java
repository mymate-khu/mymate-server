package com.mymate.mymate.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSummaryResponse {

    private Long memberId;
    private String username;
    private String email;

    private String nickname;
    private String profileImageUrl;
    private String bio;

    private boolean signUpCompleted;
}



