package com.crozhere.service.cms.layout.controller.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStationGroupsLayoutRequest {
    private List<String> stationGroupIds;
}

