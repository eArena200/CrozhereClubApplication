package com.crozhere.service.cms.club.repository.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Station {
    private String stationId;
    private String clubId;
    private String stationName;
    private StationType stationType;
    private Boolean isAvailable;
}
