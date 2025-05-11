package com.crozhere.service.cms.auth.repository.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private String phoneNumber;
    private UserRole userRole;
}
