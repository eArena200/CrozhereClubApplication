package com.crozhere.service.cms.player.controller.model.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlayerRequest {
    private String username;
    private String name;
    private String email;
}
