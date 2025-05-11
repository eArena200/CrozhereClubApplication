package com.crozhere.service.cms.auth.controller.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitAuthRequest {
    private String identifier;
}
