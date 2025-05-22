package com.crozhere.service.cms.layout.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.layout.repository.entity.StationGroupLayoutType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedStationGroupLayoutResponse {
    private String id;
    private String name;
    private String zoneLayoutId;
    private StationType stationType;
    private StationGroupLayoutType layoutType;
    private List<EnrichedStationLayoutResponse> stations;
}

