package com.crozhere.service.cms.club.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Club {
    private String id;
    private String clubAdminId;
    private String name;
}
