package com.mymate.mymate.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationClickRequest {

    @NotBlank
    private String action; // NAVIGATE, ACTION 등
}


