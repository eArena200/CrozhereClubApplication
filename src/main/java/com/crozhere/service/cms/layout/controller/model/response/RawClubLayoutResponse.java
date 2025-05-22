package com.crozhere.service.cms.layout.controller.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RawClubLayoutResponse {
    private String id;
    private Long clubId;
    private List<String> zoneIds;
}
