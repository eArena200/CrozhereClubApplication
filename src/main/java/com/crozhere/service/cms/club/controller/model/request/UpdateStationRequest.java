package com.crozhere.service.cms.club.controller.model.request;

import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStationRequest {
    private String stationName;
    private OperatingHours operatingHours;
    private Integer capacity;
}
