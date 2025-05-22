package com.crozhere.service.cms.layout.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStationLayoutRequest {
    private String stationGroupLayoutId;
    private StationType stationType;

    private Integer offsetX;
    private Integer offsetY;
    private Integer width;
    private Integer height;
}

