package com.crozhere.service.cms.club.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddStationRequest {
    private Long clubId;
    private String stationName;
    private StationType stationType;
    private String stationGroupLayoutId;
}
