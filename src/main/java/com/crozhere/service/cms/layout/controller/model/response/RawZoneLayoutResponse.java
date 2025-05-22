package com.crozhere.service.cms.layout.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawZoneLayoutResponse {
    private String id;
    private String clubLayoutId;
    private String name;
    private List<String> stationGroupLayoutIds;
}
