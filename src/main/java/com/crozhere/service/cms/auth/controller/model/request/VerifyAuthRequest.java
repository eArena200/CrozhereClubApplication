package com.crozhere.service.cms.auth.controller.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyAuthRequest {
    private String phone;
    private String otp;
}
