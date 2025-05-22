package com.crozhere.service.cms.layout.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.layout.repository.entity.StationGroupLayoutType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStationGroupLayoutRequest {
    private String zoneLayoutId;
    private String name;
    private StationType stationType;
    private StationGroupLayoutType layoutType;
}

