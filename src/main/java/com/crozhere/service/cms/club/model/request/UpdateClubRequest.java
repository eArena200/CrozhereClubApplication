package com.crozhere.service.cms.club.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateClubRequest {
    private String name;
}
