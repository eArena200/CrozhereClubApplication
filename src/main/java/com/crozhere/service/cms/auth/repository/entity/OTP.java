package com.crozhere.service.cms.auth.repository.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OTP {
    private String token;
    private OTPChannel otpChannel;
    private String identifier;
    private String otp;
    private LocalDateTime expiresAt;
}
