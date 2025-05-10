package com.crozhere.service.cms.club.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubResponse {
    private String clubId;
    private String clubAdminId;
    private String name;
}
