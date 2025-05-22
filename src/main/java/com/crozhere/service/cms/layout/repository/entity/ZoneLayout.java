package com.crozhere.service.cms.layout.repository.entity;

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
@Document(collection = "zone_layouts")
public class ZoneLayout {

    @Id
    private String id;

    private String clubLayoutId;
    private String name;
    private List<String> stationGroupLayoutIds;
}
