package com.crozhere.service.cms.player.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String name;
}
