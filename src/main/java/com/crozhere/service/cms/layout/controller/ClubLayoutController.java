package com.crozhere.service.cms.layout.controller;

import com.crozhere.service.cms.layout.controller.model.request.*;
import com.crozhere.service.cms.layout.controller.model.response.*;
import com.crozhere.service.cms.layout.service.ClubLayoutService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/layouts")
@RequiredArgsConstructor
public class ClubLayoutController {

    private final ClubLayoutService layoutService;

    // === CLUB ===

    @PostMapping("/clubs")
    public ResponseEntity<RawClubLayoutResponse> createClubLayout(
            @RequestBody CreateClubLayoutRequest request) {
        return ResponseEntity.ok(layoutService.createClubLayout(request));
    }

    @GetMapping("/clubs/{clubLayoutId}")
    public ResponseEntity<RawClubLayoutResponse> getRawClubLayout(
            @PathVariable String clubLayoutId) {
        return ResponseEntity.ok(layoutService.getRawClubLayout(clubLayoutId));
    }

    @GetMapping("/clubs/{clubLayoutId}/enriched")
    public ResponseEntity<EnrichedClubLayoutResponse> getEnrichedClubLayout(
            @PathVariable String clubLayoutId) {
        return ResponseEntity.ok(layoutService.getEnrichedClubLayout(clubLayoutId));
    }

    @DeleteMapping("/clubs/{clubLayoutId}")
    public ResponseEntity<Void> deleteClubLayout(
            @PathVariable String clubLayoutId) {
        layoutService.deleteClubLayout(clubLayoutId);
        return ResponseEntity.noContent().build();
    }

    // === ZONE  ===

    @PostMapping("/zones")
    public ResponseEntity<RawZoneLayoutResponse> addZoneLayout(
            @RequestBody AddZoneLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addZoneLayout(request));
    }

    @GetMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<RawZoneLayoutResponse> getRawZoneLayout(
            @PathVariable String zoneLayoutId) {
        return ResponseEntity.ok(layoutService.getRawZoneLayout(zoneLayoutId));
    }

    @GetMapping("/zones/{zoneLayoutId}/enriched")
    public ResponseEntity<EnrichedZoneLayoutResponse> getEnrichedZoneLayout(
            @PathVariable String zoneLayoutId) {
        return ResponseEntity.ok(layoutService.getEnrichedZoneLayout(zoneLayoutId));
    }

    @DeleteMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<Void> deleteZoneLayout(
            @PathVariable String zoneLayoutId) {
        layoutService.deleteZoneLayout(zoneLayoutId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<RawZoneLayoutResponse> updateZoneLayoutName(
            @PathVariable String zoneLayoutId,
            @RequestParam String newName) {
        return ResponseEntity.ok(layoutService.updateZoneLayoutName(zoneLayoutId, newName));
    }

    // === GROUP ===

    @PostMapping("/groups")
    public ResponseEntity<RawStationGroupLayoutResponse> addStationGroupLayout(
            @RequestBody AddStationGroupLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addStationGroupLayout(request));
    }

    @GetMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<RawStationGroupLayoutResponse> getRawStationGroupLayout(
            @PathVariable String stationGroupLayoutId) {
        return ResponseEntity.ok(layoutService.getRawStationGroupLayout(stationGroupLayoutId));
    }

    @GetMapping("/groups/{stationGroupLayoutId}/enriched")
    public ResponseEntity<EnrichedStationGroupLayoutResponse> getEnrichedStationGroupLayout(
            @PathVariable String stationGroupLayoutId) {
        return ResponseEntity.ok(layoutService.getEnrichedStationGroupLayout(stationGroupLayoutId));
    }

    @PutMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<RawStationGroupLayoutResponse> updateStationGroupLayoutName(
            @PathVariable String stationGroupLayoutId,
            @RequestParam String newName) {
        return ResponseEntity.ok(layoutService.updateStationGroupLayoutName(
                stationGroupLayoutId, newName));
    }

    @DeleteMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<Void> deleteStationGroupLayout(
            @PathVariable String stationGroupLayoutId) {
        layoutService.deleteStationGroupLayout(stationGroupLayoutId);
        return ResponseEntity.noContent().build();
    }

    // === STATION ===

    @PostMapping("/stations")
    public ResponseEntity<RawStationLayoutResponse> addStationLayout(
            @RequestBody AddStationLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addStationLayout(request));
    }

    @GetMapping("/stations/{stationLayoutId}")
    public ResponseEntity<RawStationLayoutResponse> getRawStationLayout(
            @PathVariable String stationLayoutId) {
        return ResponseEntity.ok(layoutService.getRawStationLayout(stationLayoutId));
    }

    @GetMapping("/stations/{stationLayoutId}/enriched")
    public ResponseEntity<EnrichedStationLayoutResponse> getEnrichedStationLayout(
            @PathVariable String stationLayoutId) {
        return ResponseEntity.ok(layoutService.getEnrichedStationLayout(stationLayoutId));
    }

    @PutMapping("/stations/{stationLayoutId}")
    public ResponseEntity<RawStationLayoutResponse> updateStationLayout(
            @PathVariable String stationLayoutId,
            @RequestBody UpdateStationLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateStationLayout(
                stationLayoutId, request));
    }

    @DeleteMapping("/stations/{stationLayoutId}")
    public ResponseEntity<Void> deleteStationLayout(
            @PathVariable String stationLayoutId) {
        layoutService.deleteStationLayout(stationLayoutId);
        return ResponseEntity.noContent().build();
    }
}
