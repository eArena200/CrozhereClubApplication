package com.crozhere.service.cms.club.repository.layout.entity;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "station_group_layouts")
public class StationGroupLayout {

    @Id
    private String id;

    private String name;
    private StationType stationType;
    private StationGroupLayoutType layoutType;
    private List<StationLayout> stationLayouts;
}
