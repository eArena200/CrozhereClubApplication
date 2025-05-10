package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationResponse {
    private String stationId;
    private String clubId;
    private String stationName;
    private StationType stationType;
    private Boolean isAvailable;
}
