package com.crozhere.service.cms.user.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubAdminResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
