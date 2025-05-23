package com.crozhere.service.cms.layout.service;

import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import com.crozhere.service.cms.layout.controller.model.request.*;
import com.crozhere.service.cms.layout.controller.model.response.*;
import com.crozhere.service.cms.layout.service.exception.ClubLayoutServiceException;

public interface ClubLayoutService {

    // === Club Layout ===
    RawClubLayoutResponse createClubLayout(CreateClubLayoutRequest request)
            throws ClubLayoutServiceException;
    RawClubLayoutResponse getRawClubLayout(String clubLayoutId)
            throws ClubLayoutServiceException;
    EnrichedClubLayoutResponse getEnrichedClubLayout(String clubLayoutId)
            throws ClubLayoutServiceException;
    void deleteClubLayout(String clubLayoutId) throws ClubLayoutServiceException;

    // === Zone Layout ===
    RawZoneLayoutResponse addZoneLayout(AddZoneLayoutRequest request)
            throws ClubLayoutServiceException;
    RawZoneLayoutResponse getRawZoneLayout(String zoneLayoutId)
            throws ClubLayoutServiceException;
    EnrichedZoneLayoutResponse getEnrichedZoneLayout(String zoneLayoutId)
            throws ClubLayoutServiceException;
    void deleteZoneLayout(String zoneLayoutId) throws ClubLayoutServiceException;
    RawZoneLayoutResponse updateZoneLayoutName(String zoneLayoutId, String newName)
            throws ClubLayoutServiceException;

    // === Station Group Layout ===
    RawStationGroupLayoutResponse addStationGroupLayout(AddStationGroupLayoutRequest request)
            throws ClubLayoutServiceException;
    RawStationGroupLayoutResponse getRawStationGroupLayout(String stationGroupLayoutId)
            throws ClubLayoutServiceException;
    EnrichedStationGroupLayoutResponse getEnrichedStationGroupLayout(String stationGroupLayoutId)
            throws ClubLayoutServiceException;
    RawStationGroupLayoutResponse updateStationGroupLayoutName(String stationGroupLayoutId, String newName)
            throws ClubLayoutServiceException;
    void deleteStationGroupLayout(String stationGroupLayoutId) throws ClubLayoutServiceException;

    // === Station Layout ===
    RawStationLayoutResponse addStationLayout(AddStationLayoutRequest request)
            throws ClubServiceException;
    RawStationLayoutResponse getRawStationLayout(String stationLayoutId)
            throws ClubServiceException;
    EnrichedStationLayoutResponse getEnrichedStationLayout(String stationLayoutId)
            throws ClubServiceException;
    RawStationLayoutResponse updateStationLayout(
            String stationLayoutId, UpdateStationLayoutRequest request)
            throws ClubServiceException;
    void deleteStationLayout(String stationLayoutId) throws ClubServiceException;
}
