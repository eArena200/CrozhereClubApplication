package com.crozhere.service.cms.layout.repository.entity;

import com.crozhere.service.cms.club.repository.entity.StationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "station_layouts")
public class StationLayout {

    @Id
    private String id;
    private String stationGroupLayoutId;
    private StationType stationType;
    private Long stationId;
    private Integer offsetX;
    private Integer offsetY;
    private Integer width;
    private Integer height;
}
