package com.crozhere.service.cms.layout.service;

import com.crozhere.service.cms.layout.controller.model.request.*;
import com.crozhere.service.cms.layout.controller.model.response.*;

public interface ClubLayoutService {

    // === Club Layout ===
    RawClubLayoutResponse createClubLayout(CreateClubLayoutRequest request);
    RawClubLayoutResponse getRawClubLayout(String clubLayoutId);
    EnrichedClubLayoutResponse getEnrichedClubLayout(String clubLayoutId);
    void deleteClubLayout(String clubLayoutId);

    // === Zone Layout ===
    RawZoneLayoutResponse addZoneLayout(AddZoneLayoutRequest request);
    RawZoneLayoutResponse getRawZoneLayout(String zoneLayoutId);
    EnrichedZoneLayoutResponse getEnrichedZoneLayout(String zoneLayoutId);
    void deleteZoneLayout(String zoneLayoutId);
    RawZoneLayoutResponse updateZoneLayoutName(String zoneLayoutId, String newName);

    // === Station Group Layout ===
    RawStationGroupLayoutResponse addStationGroupLayout(AddStationGroupLayoutRequest request);
    RawStationGroupLayoutResponse getRawStationGroupLayout(String stationGroupLayoutId);
    EnrichedStationGroupLayoutResponse getEnrichedStationGroupLayout(String stationGroupLayoutId);
    RawStationGroupLayoutResponse updateStationGroupLayoutName(String stationGroupLayoutId, String newName);
    void deleteStationGroupLayout(String stationGroupLayoutId);

    // === Station Layout ===
    RawStationLayoutResponse addStationLayout(AddStationLayoutRequest request);
    RawStationLayoutResponse getRawStationLayout(String stationLayoutId);
    EnrichedStationLayoutResponse getEnrichedStationLayout(String stationLayoutId);
    RawStationLayoutResponse updateStationLayout(String stationLayoutId, UpdateStationLayoutRequest request);
    void deleteStationLayout(String stationLayoutId);
}
