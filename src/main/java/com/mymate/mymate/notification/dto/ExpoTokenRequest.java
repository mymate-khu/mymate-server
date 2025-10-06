package com.mymate.mymate.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpoTokenRequest {
    @Schema(description = "Expo Push 토큰", example = "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]")
    @NotBlank
    private String token;

    @Schema(description = "디바이스 타입", example = "ANDROID")
    @NotNull
    private String deviceType; // ANDROID, IOS, WEB

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
}


