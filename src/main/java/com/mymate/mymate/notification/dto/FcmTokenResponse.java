package com.mymate.mymate.notification.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class FcmTokenResponse {
    @Schema(description = "활성 FCM 토큰 목록")
    private List<String> tokens;

    public FcmTokenResponse() {}
    public FcmTokenResponse(List<String> tokens) { this.tokens = tokens; }

    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }
}


