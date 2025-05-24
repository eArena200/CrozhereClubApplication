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
import com.crozhere.service.cms.layout.service.exception.ClubLayoutServiceException;
import com.crozhere.service.cms.layout.service.exception.ClubLayoutServiceExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public RawClubLayoutResponse createClubLayout(CreateClubLayoutRequest request)
            throws ClubLayoutServiceException {
        try {
            if (clubLayoutRepository.existsByClubId(request.getClubId())) {
                log.error("ClubLayout already exists for clubId: {}", request.getClubId());
                throw new ClubLayoutServiceException(
                        ClubLayoutServiceExceptionType.CLUB_LAYOUT_ALREADY_EXISTS);
            }

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
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in create club layout for request: {}", request.toString());
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.CREATE_CLUB_LAYOUT_FAILED);
        }
    }

    @Override
    public RawClubLayoutResponse getRawClubLayout(String clubLayoutId)
            throws ClubLayoutServiceException{
        try {
            ClubLayout clubLayout = clubLayoutRepository
                    .findById(clubLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.CLUB_LAYOUT_NOT_FOUND));

            return RawClubLayoutResponse.builder()
                    .id(clubLayout.getId())
                    .clubId(clubLayout.getClubId())
                    .zoneIds(clubLayout.getZoneLayoutIds())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting club layout for clubLayoutId: {}", clubLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.GET_CLUB_LAYOUT_FAILED);
        }
    }

    @Override
    public EnrichedClubLayoutResponse getEnrichedClubLayout(String clubLayoutId)
            throws ClubLayoutServiceException {
        try {
            ClubLayout layout = clubLayoutRepository
                    .findById(clubLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.CLUB_LAYOUT_NOT_FOUND));

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
                                                                    .stationGroupLayoutId(station.getStationGroupLayoutId())
                                                                    .stationType(station.getStationType())
                                                                    .stationId(station.getStationId())
                                                                    .offsetX(station.getOffsetX())
                                                                    .offsetY(station.getOffsetY())
                                                                    .width(station.getWidth())
                                                                    .height(station.getHeight())
                                                                    .build()
                                                    ).toList();

                                            return EnrichedStationGroupLayoutResponse.builder()
                                                    .id(group.getId())
                                                    .zoneLayoutId(group.getZoneLayoutId())
                                                    .name(group.getName())
                                                    .stationType(group.getStationType())
                                                    .layoutType(group.getLayoutType())
                                                    .stations(enrichedStations)
                                                    .build();
                                        }).toList();

                                return EnrichedZoneLayoutResponse.builder()
                                        .id(zone.getId())
                                        .clubLayoutId(zone.getClubLayoutId())
                                        .name(zone.getName())
                                        .stationGroups(enrichedGroups)
                                        .build();
                            }).toList();

            return EnrichedClubLayoutResponse.builder()
                    .id(layout.getId())
                    .clubId(layout.getClubId())
                    .zones(enrichedZones)
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in get enriched club layout for clubLayoutId: {}", clubLayoutId);
            throw new ClubLayoutServiceException(ClubLayoutServiceExceptionType.GET_CLUB_LAYOUT_FAILED);
        }
    }

    @Override
    public void deleteClubLayout(String clubLayoutId) throws ClubLayoutServiceException {
        try {
            ClubLayout clubLayout = clubLayoutRepository.findById(clubLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.CLUB_LAYOUT_NOT_FOUND));

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
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in delete club layout for clubLayoutId: {}", clubLayoutId);
            throw new ClubLayoutServiceException(ClubLayoutServiceExceptionType.DELETE_CLUB_LAYOUT_FAILED);
        }
    }


    // === ZONE ===

    @Override
    public RawZoneLayoutResponse addZoneLayout(AddZoneLayoutRequest request)
            throws ClubLayoutServiceException {
        try {
            ClubLayout clubLayout = clubLayoutRepository
                    .findById(request.getClubLayoutId())
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.CLUB_LAYOUT_NOT_FOUND));

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
                    .stationGroupLayoutIds(zone.getStationGroupLayoutIds())
                    .build();
        } catch (ClubLayoutServiceException e){
           throw e;
        } catch (Exception e){
            log.error("Exception in adding zone layout for clubLayoutId: {}",
                    request.getClubLayoutId());
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.ADD_ZONE_LAYOUT_FAILED);
        }
    }

    @Override
    public RawZoneLayoutResponse getRawZoneLayout(String zoneLayoutId)
            throws ClubLayoutServiceException {
        try {
            ZoneLayout zoneLayout = zoneLayoutRepository.findById(zoneLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.ZONE_LAYOUT_NOT_FOUND));

            return RawZoneLayoutResponse.builder()
                    .id(zoneLayout.getId())
                    .clubLayoutId(zoneLayout.getClubLayoutId())
                    .stationGroupLayoutIds(zoneLayout.getStationGroupLayoutIds())
                    .name(zoneLayout.getName())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting raw zone-layout for zoneLayoutId: {}", zoneLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.GET_ZONE_LAYOUT_FAILED);
        }
    }

    @Override
    public EnrichedZoneLayoutResponse getEnrichedZoneLayout(String zoneLayoutId) {
        try {
            ZoneLayout zone = zoneLayoutRepository.findById(zoneLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.ZONE_LAYOUT_NOT_FOUND));

            List<StationGroupLayout> groupLayouts = stationGroupLayoutRepository
                    .findAllById(zone.getStationGroupLayoutIds());

            List<EnrichedStationGroupLayoutResponse> enrichedGroups = groupLayouts.stream().map(group -> {
                List<StationLayout> stationLayouts = stationLayoutRepository.findAllById(group.getStationLayoutIds());

                List<EnrichedStationLayoutResponse> enrichedStations = stationLayouts.stream().map(station ->
                        EnrichedStationLayoutResponse.builder()
                                .id(station.getId())
                                .stationGroupLayoutId(station.getStationGroupLayoutId())
                                .stationType(station.getStationType())
                                .stationId(station.getStationId())
                                .offsetX(station.getOffsetX())
                                .offsetY(station.getOffsetY())
                                .width(station.getWidth())
                                .height(station.getHeight())
                                .build()
                ).toList();

                return EnrichedStationGroupLayoutResponse.builder()
                        .id(group.getId())
                        .zoneLayoutId(group.getZoneLayoutId())
                        .name(group.getName())
                        .stationType(group.getStationType())
                        .layoutType(group.getLayoutType())
                        .stations(enrichedStations)
                        .build();
            }).toList();

            return EnrichedZoneLayoutResponse.builder()
                    .id(zone.getId())
                    .clubLayoutId(zone.getClubLayoutId())
                    .name(zone.getName())
                    .stationGroups(enrichedGroups)
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting enriched zone-layout for zoneLayoutId: {}", zoneLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.GET_ZONE_LAYOUT_FAILED);
        }
    }

    @Override
    public RawZoneLayoutResponse updateZoneLayout(String zoneLayoutId, UpdateZoneLayoutRequest request)
            throws ClubLayoutServiceException {
        try {
            ZoneLayout zoneLayout = zoneLayoutRepository.findById(zoneLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.ZONE_LAYOUT_NOT_FOUND));

            if(StringUtils.hasText(request.getName())){
                zoneLayout.setName(request.getName());
            }

            zoneLayoutRepository.save(zoneLayout);

            return RawZoneLayoutResponse.builder()
                    .id(zoneLayout.getId())
                    .clubLayoutId(zoneLayout.getClubLayoutId())
                    .name(zoneLayout.getName())
                    .stationGroupLayoutIds(zoneLayout.getStationGroupLayoutIds())
                    .build();

        } catch (ClubLayoutServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in updating zone-layout for zoneLayoutId: {}", zoneLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.UPDATE_ZONE_LAYOUT_FAILED);
        }
    }

    @Override
    public void deleteZoneLayout(String zoneLayoutId) throws ClubLayoutServiceException {
        try {
            ZoneLayout zoneLayout = zoneLayoutRepository.findById(zoneLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.ZONE_LAYOUT_NOT_FOUND));

            ClubLayout clubLayout = clubLayoutRepository.findById(zoneLayout.getClubLayoutId())
                    .orElseThrow(() -> new IllegalArgumentException("ClubLayout not found"));

            clubLayout.getZoneLayoutIds().removeIf(zoneLayoutId::equals);
            clubLayoutRepository.save(clubLayout);

            List<StationGroupLayout> groups = stationGroupLayoutRepository.findAllById(zoneLayout.getStationGroupLayoutIds());
            groups.forEach(group -> stationLayoutRepository.deleteAllById(group.getStationLayoutIds()));
            stationGroupLayoutRepository.deleteAllById(zoneLayout.getStationGroupLayoutIds());

            zoneLayoutRepository.deleteById(zoneLayoutId);
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in deleting zone-layout for zoneLayoutId: {}", zoneLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.DELETE_ZONE_LAYOUT_FAILED);
        }
    }


    // === GROUP ===

    @Override
    public RawStationGroupLayoutResponse addStationGroupLayout(
            AddStationGroupLayoutRequest request) throws ClubLayoutServiceException {

        try {
            ZoneLayout zoneLayout = zoneLayoutRepository
                    .findById(request.getZoneLayoutId())
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.ZONE_LAYOUT_NOT_FOUND));


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
                    .stationLayoutIds(group.getStationLayoutIds())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in adding station-group-layout for zoneLayoutId: {}",
                    request.getZoneLayoutId());
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.ADD_GROUP_LAYOUT_FAILED);
        }
    }

    @Override
    public RawStationGroupLayoutResponse getRawStationGroupLayout(String stationGroupLayoutId)
            throws ClubLayoutServiceException {
        try {
            StationGroupLayout stationGroupLayout =
                    stationGroupLayoutRepository.findById(stationGroupLayoutId)
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.GROUP_LAYOUT_NOT_FOUND));

            return RawStationGroupLayoutResponse.builder()
                    .id(stationGroupLayout.getId())
                    .zoneLayoutId(stationGroupLayout.getZoneLayoutId())
                    .layoutType(stationGroupLayout.getLayoutType())
                    .stationLayoutIds(stationGroupLayout.getStationLayoutIds())
                    .stationType(stationGroupLayout.getStationType())
                    .name(stationGroupLayout.getName())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting raw station-group-layout for groupLayoutId: {}",
                    stationGroupLayoutId);
            throw new ClubLayoutServiceException(ClubLayoutServiceExceptionType.GET_GROUP_LAYOUT_FAILED);
        }
    }

    @Override
    public EnrichedStationGroupLayoutResponse getEnrichedStationGroupLayout(String stationGroupLayoutId)
            throws ClubLayoutServiceException {
        try {
            StationGroupLayout group =
                    stationGroupLayoutRepository.findById(stationGroupLayoutId)
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.GROUP_LAYOUT_NOT_FOUND));

            List<StationLayout> stationLayouts =
                    stationLayoutRepository.findAllById(group.getStationLayoutIds());

            List<EnrichedStationLayoutResponse> enrichedStations =
                    stationLayouts.stream()
                            .map(station ->
                                    EnrichedStationLayoutResponse.builder()
                                            .id(station.getId())
                                            .stationGroupLayoutId(station
                                                    .getStationGroupLayoutId())
                                            .stationType(station.getStationType())
                                            .stationId(station.getStationId())
                                            .offsetX(station.getOffsetX())
                                            .offsetY(station.getOffsetY())
                                            .width(station.getWidth())
                                            .height(station.getHeight())
                                            .build()
                            ).toList();

            return EnrichedStationGroupLayoutResponse.builder()
                    .id(group.getId())
                    .zoneLayoutId(group.getZoneLayoutId())
                    .name(group.getName())
                    .stationType(group.getStationType())
                    .layoutType(group.getLayoutType())
                    .stations(enrichedStations)
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting enriched station-group-layout for groupLayoutId: {}",
                    stationGroupLayoutId);
            throw new ClubLayoutServiceException(ClubLayoutServiceExceptionType.GET_GROUP_LAYOUT_FAILED);
        }
    }

    @Override
    public RawStationGroupLayoutResponse updateStationGroupLayout(
            String stationGroupLayoutId, UpdateStationGroupLayoutRequest request)
            throws ClubLayoutServiceException {
        try {
            StationGroupLayout stationGroupLayout =
                    stationGroupLayoutRepository.findById(stationGroupLayoutId)
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.GROUP_LAYOUT_NOT_FOUND));


            if(StringUtils.hasText(request.getName())){
                stationGroupLayout.setName(request.getName());
            }

            stationGroupLayoutRepository.save(stationGroupLayout);

            return RawStationGroupLayoutResponse.builder()
                    .id(stationGroupLayout.getId())
                    .zoneLayoutId(stationGroupLayout.getZoneLayoutId())
                    .name(stationGroupLayout.getName())
                    .layoutType(stationGroupLayout.getLayoutType())
                    .stationType(stationGroupLayout.getStationType())
                    .stationLayoutIds(stationGroupLayout.getStationLayoutIds())
                    .build();

        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in updating group-layout for zoneLayoutId: {}", stationGroupLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.UPDATE_GROUP_LAYOUT_FAILED);
        }
    }

    @Override
    public void deleteStationGroupLayout(String stationGroupLayoutId)
            throws ClubLayoutServiceException {
        try {
            StationGroupLayout stationGroupLayout =
                    stationGroupLayoutRepository.findById(stationGroupLayoutId)
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.GROUP_LAYOUT_NOT_FOUND));

            ZoneLayout zoneLayout =
                    zoneLayoutRepository.findById(stationGroupLayout.getZoneLayoutId())
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.ZONE_LAYOUT_NOT_FOUND));

            zoneLayout.getStationGroupLayoutIds().removeIf(stationGroupLayoutId::equals);
            zoneLayoutRepository.save(zoneLayout);

            List<String> stationIds = stationGroupLayout.getStationLayoutIds();
            stationLayoutRepository.deleteAllById(stationIds);

            stationGroupLayoutRepository.deleteById(stationGroupLayoutId);
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in deleting group-layout for zoneLayoutId: {}",
                    stationGroupLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.DELETE_GROUP_LAYOUT_FAILED);
        }
    }

    // === STATION ===

    @Override
    public RawStationLayoutResponse addStationLayout(AddStationLayoutRequest request)
            throws ClubLayoutServiceException {
        try {
            if(stationLayoutRepository.existsByStationId(request.getStationId())){
                log.error("StationLayout already exists for stationId: {}",
                        request.getStationId());
                throw new ClubLayoutServiceException(
                        ClubLayoutServiceExceptionType.STATION_LAYOUT_ALREADY_EXISTS);
            }

            StationGroupLayout group = stationGroupLayoutRepository
                    .findById(request.getStationGroupLayoutId())
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.GROUP_LAYOUT_NOT_FOUND));

            String stationLayoutId = UUID.randomUUID().toString();

            StationLayout station = StationLayout.builder()
                    .id(stationLayoutId)
                    .stationGroupLayoutId(group.getId())
                    .stationType(request.getStationType())
                    .stationId(request.getStationId())
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
                    .stationId(station.getStationId())
                    .offsetX(station.getOffsetX())
                    .offsetY(station.getOffsetY())
                    .width(station.getWidth())
                    .height(station.getHeight())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in adding station-layout for stationGroupLayoutId: {}",
                    request.getStationGroupLayoutId());
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.ADD_STATION_LAYOUT_FAILED);
        }
    }

    @Override
    public RawStationLayoutResponse getRawStationLayout(String stationLayoutId)
            throws ClubLayoutServiceException {
        try {
            StationLayout stationLayout = stationLayoutRepository.findById(stationLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.STATION_LAYOUT_NOT_FOUND));

            return RawStationLayoutResponse.builder()
                    .id(stationLayout.getId())
                    .stationGroupLayoutId(stationLayout.getStationGroupLayoutId())
                    .stationType(stationLayout.getStationType())
                    .stationId(stationLayout.getStationId())
                    .offsetX(stationLayout.getOffsetX())
                    .offsetY(stationLayout.getOffsetY())
                    .width(stationLayout.getWidth())
                    .height(stationLayout.getHeight())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting raw station-layout for stationLayoutId: {}",
                    stationLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.GET_STATION_LAYOUT_FAILED);
        }
    }

    @Override
    public EnrichedStationLayoutResponse getEnrichedStationLayout(String stationLayoutId)
            throws ClubLayoutServiceException {
        try {
            StationLayout stationLayout =
                    stationLayoutRepository.findById(stationLayoutId)
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.STATION_LAYOUT_NOT_FOUND));

            return EnrichedStationLayoutResponse.builder()
                    .id(stationLayout.getId())
                    .stationGroupLayoutId(stationLayout.getStationGroupLayoutId())
                    .stationType(stationLayout.getStationType())
                    .stationId(stationLayout.getStationId())
                    .offsetX(stationLayout.getOffsetX())
                    .offsetY(stationLayout.getOffsetY())
                    .height(stationLayout.getHeight())
                    .width(stationLayout.getWidth())
                    .build();
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in getting enriched station-layout for stationLayoutId: {}",
                    stationLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.GET_STATION_LAYOUT_FAILED);
        }
    }

    @Override
    public RawStationLayoutResponse updateStationLayout(
            String stationLayoutId, UpdateStationLayoutRequest request)
            throws ClubLayoutServiceException {

        try {
            StationLayout station = stationLayoutRepository.findById(stationLayoutId)
                    .orElseThrow(() -> new ClubLayoutServiceException(
                            ClubLayoutServiceExceptionType.STATION_LAYOUT_NOT_FOUND));

            if(hasValue(request.getOffsetX())){
                station.setOffsetX(request.getOffsetX());
            }

            if(hasValue(request.getOffsetY())){
                station.setOffsetY(request.getOffsetY());
            }

            if(hasValue(request.getHeight())){
                station.setHeight(request.getHeight());
            }

            if(hasValue(request.getWidth())){
                station.setWidth(request.getWidth());
            }

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
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in updating station-layout for stationLayoutId: {}", stationLayoutId);
            throw new ClubLayoutServiceException(ClubLayoutServiceExceptionType.UPDATE_STATION_LAYOUT_FAILED);
        }
    }

    @Override
    public void deleteStationLayout(String stationLayoutId)
            throws ClubLayoutServiceException {
        try {
            StationLayout stationLayout =
                    stationLayoutRepository.findById(stationLayoutId)
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.STATION_LAYOUT_NOT_FOUND));

            StationGroupLayout stationGroupLayout =
                    stationGroupLayoutRepository.findById(stationLayout.getStationGroupLayoutId())
                            .orElseThrow(() -> new ClubLayoutServiceException(
                                    ClubLayoutServiceExceptionType.GROUP_LAYOUT_NOT_FOUND));

            stationGroupLayout.getStationLayoutIds()
                    .removeIf(stationLayoutId::equals);
            stationGroupLayoutRepository.save(stationGroupLayout);

            stationLayoutRepository.deleteById(stationLayoutId);

            log.info("Deleted StationLayout {} from group {}",
                    stationLayoutId, stationGroupLayout.getId());
        } catch (ClubLayoutServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Exception in deleting station-layout for stationLayoutId: {}", stationLayoutId);
            throw new ClubLayoutServiceException(
                    ClubLayoutServiceExceptionType.DELETE_STATION_LAYOUT_FAILED);
        }
    }


    private boolean hasValue(Integer integer){
        return integer != null;
    }
}
