package com.crozhere.service.cms.club.controller.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubResponse {
    private Long clubId;
    private Long clubAdminId;
    private String clubLayoutId;
    private String name;
}
