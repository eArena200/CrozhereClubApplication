package com.crozhere.service.cms.user.repository.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubAdmin {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
}
