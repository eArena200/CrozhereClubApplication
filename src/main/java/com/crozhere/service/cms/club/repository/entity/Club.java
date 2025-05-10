package com.crozhere.service.cms.club.repository.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Club {
    private String clubId;
    private String clubAdminId;
    private String name;
}
