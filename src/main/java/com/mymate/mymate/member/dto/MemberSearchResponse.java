package com.mymate.mymate.member.dto;

import com.mymate.mymate.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSearchResponse {
    private Long id;
    private String userId;
    private String username;
    private String email;
    
    public MemberSearchResponse(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId();
        this.username = member.getUsername();
        this.email = member.getEmail();
    }
}