package com.crozhere.service.cms.auth.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyAuthResponse {
    private Boolean isAllowed;
    private String userId;
    private String jwt;
}
