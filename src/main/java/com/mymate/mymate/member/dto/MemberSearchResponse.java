package com.mymate.mymate.member.dto;

import com.mymate.mymate.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSearchResponse {

    private Long id;
    private String username;
    private String name;
    private String email;

    public MemberSearchResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
