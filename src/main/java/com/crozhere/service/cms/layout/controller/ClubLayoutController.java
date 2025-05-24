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

    @GetMapping("/clubs/{clubLayoutId}")
    public ResponseEntity<?> getClubLayout(
            @PathVariable String clubLayoutId,
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedClubLayout(clubLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawClubLayout(clubLayoutId));
        }
    }

    // === ZONE  ===

    @PostMapping("/zones")
    public ResponseEntity<RawZoneLayoutResponse> addZoneLayout(
            @RequestBody AddZoneLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addZoneLayout(request));
    }

    @GetMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<?> getZoneLayout(
            @PathVariable String zoneLayoutId,
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedZoneLayout(zoneLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawZoneLayout(zoneLayoutId));
        }
    }

    @PutMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<RawZoneLayoutResponse> updateZoneLayout(
            @PathVariable String zoneLayoutId,
            @RequestBody UpdateZoneLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateZoneLayout(zoneLayoutId, request));
    }

    @DeleteMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<Void> deleteZoneLayout(
            @PathVariable String zoneLayoutId) {
        layoutService.deleteZoneLayout(zoneLayoutId);
        return ResponseEntity.noContent().build();
    }

    // === GROUP ===

    @PostMapping("/groups")
    public ResponseEntity<RawStationGroupLayoutResponse> addStationGroupLayout(
            @RequestBody AddStationGroupLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addStationGroupLayout(request));
    }

    @GetMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<?> getStationGroupLayout(
            @PathVariable String stationGroupLayoutId,
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedStationGroupLayout(stationGroupLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawStationGroupLayout(stationGroupLayoutId));
        }
    }

    @PutMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<RawStationGroupLayoutResponse> updateStationGroupLayout(
            @PathVariable String stationGroupLayoutId,
            @RequestBody UpdateStationGroupLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateStationGroupLayout(stationGroupLayoutId, request));
    }

    @DeleteMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<Void> deleteStationGroupLayout(
            @PathVariable String stationGroupLayoutId) {
        layoutService.deleteStationGroupLayout(stationGroupLayoutId);
        return ResponseEntity.noContent().build();
    }

    // === STATION ===

    @GetMapping("/stations/{stationLayoutId}")
    public ResponseEntity<?> getStationLayout(
            @PathVariable String stationLayoutId,
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedStationLayout(stationLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawStationLayout(stationLayoutId));
        }
    }

    @PutMapping("/stations/{stationLayoutId}")
    public ResponseEntity<RawStationLayoutResponse> updateStationLayout(
            @PathVariable String stationLayoutId,
            @RequestBody UpdateStationLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateStationLayout(
                stationLayoutId, request));
    }
}
