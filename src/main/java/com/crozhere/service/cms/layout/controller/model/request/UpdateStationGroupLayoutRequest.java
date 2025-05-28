package com.crozhere.service.cms.layout.controller.model.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStationGroupLayoutRequest {
    private String name;
}
