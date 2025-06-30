package com.crozhere.service.cms.auth.controller.model.response;

import com.crozhere.service.cms.user.repository.entity.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyAuthResponse {
    private String jwt;
    private Long userId;
    private Long playerId;
    private Long clubAdminId;
    private UserRole role;
}
