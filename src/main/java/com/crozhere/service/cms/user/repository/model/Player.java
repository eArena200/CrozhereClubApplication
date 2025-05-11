package com.crozhere.service.cms.user.repository.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Player {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
}
