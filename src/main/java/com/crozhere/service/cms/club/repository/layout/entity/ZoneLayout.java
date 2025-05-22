package com.crozhere.service.cms.club.repository.layout.entity;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "zone_layouts")
public class ZoneLayout {

    @Id
    private String id;

    private String name;
    private List<StationGroupLayout> stationGroupLayouts;
    private List<StationLayout> standaloneStationLayouts;
}
