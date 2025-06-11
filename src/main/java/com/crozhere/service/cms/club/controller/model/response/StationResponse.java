package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationResponse {
    private Long stationId;
    private Long clubId;
    private String stationName;
    private StationType stationType;
    private OperatingHours operatingHours;
    private Integer capacity;
    private Boolean isActive;
}
