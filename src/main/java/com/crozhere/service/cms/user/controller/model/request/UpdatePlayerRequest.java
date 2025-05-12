package com.crozhere.service.cms.user.controller.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePlayerRequest {
    private String username;
    private String name;
    private String email;
}
