package com.crozhere.service.cms.auth.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitAuthResponse {
    private String token;
}
