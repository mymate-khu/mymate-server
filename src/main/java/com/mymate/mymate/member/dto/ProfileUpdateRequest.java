package com.mymate.mymate.member.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {

    @Size(max = 40)
    private String nickname;

    private String profileImageUrl;

    @Size(max = 200)
    private String bio;
}



