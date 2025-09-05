package com.mymate.mymate.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneCodeVerifyRequest {
    private String phone;
    private String code;
}
