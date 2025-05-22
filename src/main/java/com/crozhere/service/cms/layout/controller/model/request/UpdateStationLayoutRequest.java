package com.crozhere.service.cms.layout.controller.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStationLayoutRequest {
    private Integer offsetX;
    private Integer offsetY;
    private Integer width;
    private Integer height;
}
