package com.crozhere.service.cms.user.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerResponse {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String name;
}
