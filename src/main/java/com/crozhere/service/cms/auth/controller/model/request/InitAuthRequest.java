package com.crozhere.service.cms.auth.controller.model.request;

import com.crozhere.service.cms.auth.repository.entity.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitAuthRequest {
    private String phone;
    private UserRole role;
}
