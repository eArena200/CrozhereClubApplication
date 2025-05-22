package com.crozhere.service.cms.layout.service;

import com.crozhere.service.cms.layout.controller.model.request.*;
import com.crozhere.service.cms.layout.controller.model.response.*;
import com.crozhere.service.cms.layout.repository.StationGroupLayoutRepository;
import com.crozhere.service.cms.layout.repository.StationLayoutRepository;
import com.crozhere.service.cms.layout.repository.ZoneLayoutRepository;
import com.crozhere.service.cms.layout.repository.entity.ClubLayout;
import com.crozhere.service.cms.layout.repository.ClubLayoutRepository;
import com.crozhere.service.cms.layout.repository.entity.StationGroupLayout;
import com.crozhere.service.cms.layout.repository.entity.StationLayout;
import com.crozhere.service.cms.layout.repository.entity.ZoneLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubLayoutServiceImpl implements ClubLayoutService {

    private final ClubLayoutRepository clubLayoutRepository;
    private final ZoneLayoutRepository zoneLayoutRepository;
    private final StationGroupLayoutRepository stationGroupLayoutRepository;
    private final StationLayoutRepository stationLayoutRepository;


    // === CLUB ===

    @Override
    public RawClubLayoutResponse createClubLayout(CreateClubLayoutRequest request) {
        // TODO: Validate any existing id against the provided clubId before creating new.
        ClubLayout layout = ClubLayout.builder()
                .id(UUID.randomUUID().toString())
                .clubId(request.getClubId())
                .zoneLayoutIds(new ArrayList<>())
                .build();

        clubLayoutRepository.save(layout);

        return RawClubLayoutResponse.builder()
                        .id(layout.getId())
                        .clubId(layout.getClubId())
                        .build();
    }

    @Override
    public RawClubLayoutResponse getRawClubLayout(String clubLayoutId) {
        ClubLayout clubLayout = clubLayoutRepository
                .findById(clubLayoutId)
                .orElseThrow(
                        () -> new IllegalArgumentException("ClubLayout not found")
                );

        return RawClubLayoutResponse.builder()
                .id(clubLayout.getId())
                .clubId(clubLayout.getClubId())
                .zoneIds(clubLayout.getZoneLayoutIds())
                .build();
    }

    @Override
    public EnrichedClubLayoutResponse getEnrichedClubLayout(String clubLayoutId) {
        ClubLayout layout = clubLayoutRepository
                .findById(clubLayoutId)
                .orElseThrow(
                        () -> new IllegalArgumentException("ClubLayout not found")
                );

        List<ZoneLayout> zoneLayouts = zoneLayoutRepository
                .findAllById(layout.getZoneLayoutIds());

        List<EnrichedZoneLayoutResponse> enrichedZones =
                zoneLayouts.stream()
                        .map(zone -> {
                            List<StationGroupLayout> groupLayouts =
                                    stationGroupLayoutRepository.findAllById(
                                            zone.getStationGroupLayoutIds());

                            List<EnrichedStationGroupLayoutResponse> enrichedGroups =
                                    groupLayouts.stream().map(group -> {
                                        List<StationLayout> stationLayouts =
                                                stationLayoutRepository.findAllById(
                                                        group.getStationLayoutIds());

                                        List<EnrichedStationLayoutResponse> enrichedStations =
                                                stationLayouts.stream().map(station ->
                                                        EnrichedStationLayoutResponse.builder()
                                                                .id(station.getId())
                                                                .stationType(station.getStationType())
                                                                .offsetX(station.getOffsetX())
                                                                .offsetY(station.getOffsetY())
                                                                .width(station.getWidth())
                                                                .height(station.getHeight())
                                                                .build()
                                                ).toList();

                                        return EnrichedStationGroupLayoutResponse.builder()
                                                .id(group.getId())
                                                .name(group.getName())
                                                .stationType(group.getStationType())
                                                .layoutType(group.getLayoutType())
                                                .stations(enrichedStations)
                                                .build();
                                    }).toList();

                            return EnrichedZoneLayoutResponse.builder()
                                    .id(zone.getId())
                                    .name(zone.getName())
                                    .stationGroups(enrichedGroups)
                                    .build();
                        }).toList();

        return EnrichedClubLayoutResponse.builder()
                .id(layout.getId())
                .zones(enrichedZones)
                .build();
    }

    @Override
    public void deleteClubLayout(String clubLayoutId) {
        ClubLayout clubLayout = clubLayoutRepository.findById(clubLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("ClubLayout not found"));

        List<ZoneLayout> zones = zoneLayoutRepository.findAllById(clubLayout.getZoneLayoutIds());

        for (ZoneLayout zone : zones) {
            List<StationGroupLayout> groups = stationGroupLayoutRepository
                    .findAllById(zone.getStationGroupLayoutIds());
            groups.forEach(group -> stationLayoutRepository
                    .deleteAllById(group.getStationLayoutIds()));
            stationGroupLayoutRepository.deleteAllById(zone.getStationGroupLayoutIds());
        }

        zoneLayoutRepository.deleteAllById(clubLayout.getZoneLayoutIds());
        clubLayoutRepository.deleteById(clubLayoutId);
    }


    // === ZONE ===

    @Override
    public RawZoneLayoutResponse addZoneLayout(AddZoneLayoutRequest request) {

        ClubLayout clubLayout = clubLayoutRepository
                .findById(request.getClubLayoutId())
                .orElseThrow(() -> new IllegalArgumentException("ClubLayout not found"));

        String zoneId = UUID.randomUUID().toString();

        ZoneLayout zone = ZoneLayout.builder()
                .id(zoneId)
                .clubLayoutId(clubLayout.getId())
                .name(request.getName())
                .stationGroupLayoutIds(new ArrayList<>())
                .build();

        zoneLayoutRepository.save(zone);

        clubLayout.getZoneLayoutIds().add(zoneId);
        clubLayoutRepository.save(clubLayout);

        return RawZoneLayoutResponse.builder()
                .id(zoneId)
                .clubLayoutId(zone.getClubLayoutId())
                .name(zone.getName())
                .build();
    }

    @Override
    public RawZoneLayoutResponse getRawZoneLayout(String zoneLayoutId) {
        ZoneLayout zoneLayout = zoneLayoutRepository.findById(zoneLayoutId)
                .orElseThrow(
                        () -> new IllegalArgumentException("ZoneLayout not found")
                );

        return RawZoneLayoutResponse.builder()
                .id(zoneLayout.getId())
                .clubLayoutId(zoneLayout.getClubLayoutId())
                .stationGroupLayoutIds(zoneLayout.getStationGroupLayoutIds())
                .name(zoneLayout.getName())
                .build();
    }

    @Override
    public EnrichedZoneLayoutResponse getEnrichedZoneLayout(String zoneLayoutId) {
        ZoneLayout zone = zoneLayoutRepository.findById(zoneLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("ZoneLayout not found"));

        List<StationGroupLayout> groupLayouts = stationGroupLayoutRepository
                .findAllById(zone.getStationGroupLayoutIds());

        List<EnrichedStationGroupLayoutResponse> enrichedGroups = groupLayouts.stream().map(group -> {
            List<StationLayout> stationLayouts = stationLayoutRepository.findAllById(group.getStationLayoutIds());

            List<EnrichedStationLayoutResponse> enrichedStations = stationLayouts.stream().map(station ->
                    EnrichedStationLayoutResponse.builder()
                            .id(station.getId())
                            .stationType(station.getStationType())
                            .offsetX(station.getOffsetX())
                            .offsetY(station.getOffsetY())
                            .width(station.getWidth())
                            .height(station.getHeight())
                            .build()
            ).toList();

            return EnrichedStationGroupLayoutResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .stationType(group.getStationType())
                    .layoutType(group.getLayoutType())
                    .stations(enrichedStations)
                    .build();
        }).toList();

        return EnrichedZoneLayoutResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .stationGroups(enrichedGroups)
                .build();
    }

    @Override
    public RawZoneLayoutResponse updateZoneLayoutName(String zoneLayoutId, String newName) {
        return null;
    }

    @Override
    public void deleteZoneLayout(String zoneLayoutId) {
        ZoneLayout zoneLayout = zoneLayoutRepository.findById(zoneLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("ZoneLayout not found"));

        ClubLayout clubLayout = clubLayoutRepository.findById(zoneLayout.getClubLayoutId())
                .orElseThrow(() -> new IllegalArgumentException("ClubLayout not found"));

        clubLayout.getZoneLayoutIds().removeIf(zoneLayoutId::equals);
        clubLayoutRepository.save(clubLayout);

        List<StationGroupLayout> groups = stationGroupLayoutRepository.findAllById(zoneLayout.getStationGroupLayoutIds());
        groups.forEach(group -> stationLayoutRepository.deleteAllById(group.getStationLayoutIds()));
        stationGroupLayoutRepository.deleteAllById(zoneLayout.getStationGroupLayoutIds());

        zoneLayoutRepository.deleteById(zoneLayoutId);
    }


    // === GROUP ===

    @Override
    public RawStationGroupLayoutResponse addStationGroupLayout(
            AddStationGroupLayoutRequest request) {

        ZoneLayout zoneLayout = zoneLayoutRepository
                .findById(request.getZoneLayoutId())
                .orElseThrow(() -> new IllegalArgumentException("ZoneLayout not found"));


        String groupId = UUID.randomUUID().toString();

        StationGroupLayout group = StationGroupLayout.builder()
                .id(groupId)
                .zoneLayoutId(zoneLayout.getId())
                .name(request.getName())
                .stationType(request.getStationType())
                .layoutType(request.getLayoutType())
                .stationLayoutIds(new ArrayList<>())
                .build();

        stationGroupLayoutRepository.save(group);

        zoneLayout.getStationGroupLayoutIds().add(groupId);
        zoneLayoutRepository.save(zoneLayout);

        return RawStationGroupLayoutResponse.builder()
                .id(groupId)
                .zoneLayoutId(group.getZoneLayoutId())
                .name(group.getName())
                .stationType(group.getStationType())
                .layoutType(group.getLayoutType())
                .build();
    }

    @Override
    public RawStationGroupLayoutResponse getRawStationGroupLayout(String stationGroupLayoutId) {
        StationGroupLayout stationGroupLayout =
                stationGroupLayoutRepository.findById(stationGroupLayoutId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("StationGroupLayout not found")
                        );

        return RawStationGroupLayoutResponse.builder()
                .id(stationGroupLayout.getId())
                .zoneLayoutId(stationGroupLayout.getZoneLayoutId())
                .layoutType(stationGroupLayout.getLayoutType())
                .stationLayoutIds(stationGroupLayout.getStationLayoutIds())
                .stationType(stationGroupLayout.getStationType())
                .name(stationGroupLayout.getName())
                .build();
    }

    @Override
    public EnrichedStationGroupLayoutResponse getEnrichedStationGroupLayout(String stationGroupLayoutId) {
        StationGroupLayout group = stationGroupLayoutRepository.findById(stationGroupLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("StationGroupLayout not found"));

        List<StationLayout> stationLayouts =
                stationLayoutRepository.findAllById(group.getStationLayoutIds());

        List<EnrichedStationLayoutResponse> enrichedStations =
                stationLayouts.stream()
                        .map(station ->
                                EnrichedStationLayoutResponse.builder()
                                        .id(station.getId())
                                        .stationType(station.getStationType())
                                        .offsetX(station.getOffsetX())
                                        .offsetY(station.getOffsetY())
                                        .width(station.getWidth())
                                        .height(station.getHeight())
                                        .build()
                        ).toList();

        return EnrichedStationGroupLayoutResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .stationType(group.getStationType())
                .layoutType(group.getLayoutType())
                .stations(enrichedStations)
                .build();
    }

    @Override
    public RawStationGroupLayoutResponse updateStationGroupLayoutName(String stationGroupLayoutId, String newName) {
        return null;
    }

    @Override
    public void deleteStationGroupLayout(String stationGroupLayoutId) {
        StationGroupLayout stationGroupLayout = stationGroupLayoutRepository.findById(stationGroupLayoutId)
                .orElseThrow(
                        () -> new IllegalArgumentException("StationGroupLayout not found")
                );

        ZoneLayout zoneLayout = zoneLayoutRepository.findById(stationGroupLayout.getZoneLayoutId())
                .orElseThrow(
                        () -> new IllegalArgumentException("ZoneLayout not found")
                );

        zoneLayout.getStationGroupLayoutIds().removeIf(stationGroupLayoutId::equals);
        zoneLayoutRepository.save(zoneLayout);

        List<String> stationIds = stationGroupLayout.getStationLayoutIds();
        stationLayoutRepository.deleteAllById(stationIds);

        stationGroupLayoutRepository.deleteById(stationGroupLayoutId);
    }

    // === STATION ===

    @Override
    public RawStationLayoutResponse addStationLayout(AddStationLayoutRequest request) {

        StationGroupLayout group = stationGroupLayoutRepository
                .findById(request.getStationGroupLayoutId())
                .orElseThrow(() -> new IllegalArgumentException("StationGroupLayout not found"));

        String stationLayoutId = UUID.randomUUID().toString();

        StationLayout station = StationLayout.builder()
                .id(stationLayoutId)
                .stationGroupLayoutId(group.getId())
                .stationType(request.getStationType())
                .offsetX(request.getOffsetX())
                .offsetY(request.getOffsetY())
                .width(request.getWidth())
                .height(request.getHeight())
                .build();

        stationLayoutRepository.save(station);

        group.getStationLayoutIds().add(station.getId());
        stationGroupLayoutRepository.save(group);

        return RawStationLayoutResponse.builder()
                .id(station.getId())
                .stationGroupLayoutId(station.getStationGroupLayoutId())
                .stationType(station.getStationType())
                .offsetX(station.getOffsetX())
                .offsetY(station.getOffsetY())
                .width(station.getWidth())
                .height(station.getHeight())
                .build();
    }

    @Override
    public RawStationLayoutResponse getRawStationLayout(String stationLayoutId) {
        StationLayout stationLayout = stationLayoutRepository.findById(stationLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("StationLayout not found"));

        return RawStationLayoutResponse.builder()
                .id(stationLayout.getId())
                .stationGroupLayoutId(stationLayout.getStationGroupLayoutId())
                .stationType(stationLayout.getStationType())
                .offsetX(stationLayout.getOffsetX())
                .offsetY(stationLayout.getOffsetY())
                .width(stationLayout.getWidth())
                .height(stationLayout.getHeight())
                .build();
    }

    @Override
    public EnrichedStationLayoutResponse getEnrichedStationLayout(String stationLayoutId) {
        StationLayout stationLayout =
                stationLayoutRepository.findById(stationLayoutId)
                .orElseThrow(
                        () -> new IllegalArgumentException("StationLayout not found")
                );

        return EnrichedStationLayoutResponse.builder()
                .id(stationLayout.getId())
                .stationGroupLayoutId(stationLayout.getStationGroupLayoutId())
                .stationType(stationLayout.getStationType())
                .offsetX(stationLayout.getOffsetX())
                .offsetY(stationLayout.getOffsetY())
                .height(stationLayout.getHeight())
                .width(stationLayout.getWidth())
                .build();
    }


    @Override
    public RawStationLayoutResponse updateStationLayout(
            String stationLayoutId, UpdateStationLayoutRequest request) {

        StationLayout station = stationLayoutRepository.findById(stationLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("StationLayout not found"));

        // TODO: MISSING GROUP UPDATE AS OF NOW
        station.setOffsetX(request.getOffsetX());
        station.setOffsetY(request.getOffsetY());
        station.setWidth(request.getWidth());
        station.setHeight(request.getHeight());

        stationLayoutRepository.save(station);

        return RawStationLayoutResponse.builder()
                .id(station.getId())
                .stationGroupLayoutId((station.getStationGroupLayoutId()))
                .stationType(station.getStationType())
                .offsetX(station.getOffsetX())
                .offsetY(station.getOffsetY())
                .width(station.getWidth())
                .height(station.getHeight())
                .build();
    }

    @Override
    public void deleteStationLayout(String stationLayoutId) {
        StationLayout stationLayout = stationLayoutRepository
                .findById(stationLayoutId)
                .orElseThrow(() -> new IllegalArgumentException("StationLayout not found"));

        StationGroupLayout stationGroupLayout =
                stationGroupLayoutRepository
                        .findById(stationLayout.getStationGroupLayoutId())
                        .orElseThrow(() -> new IllegalArgumentException("StationGroupLayout not found"));

        stationGroupLayout.getStationLayoutIds()
                .removeIf(stationLayoutId::equals);
        stationGroupLayoutRepository.save(stationGroupLayout);

        stationLayoutRepository.deleteById(stationLayoutId);

        log.info("Deleted StationLayout {} from group {}",
                stationLayoutId, stationGroupLayout.getId());
    }

}
