package com.crozhere.service.cms.layout.controller;

import com.crozhere.service.cms.layout.controller.model.request.*;
import com.crozhere.service.cms.layout.controller.model.response.*;
import com.crozhere.service.cms.layout.service.ClubLayoutService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/layouts")
@RequiredArgsConstructor
@Tag(name = "Club Layout Management", description = "APIs for managing club layouts, zones, station groups, and individual stations")
public class ClubLayoutController {

    private final ClubLayoutService layoutService;

    @Operation(
        summary = "Get club layout",
        description = "Retrieves the layout of a club, optionally with enriched data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved club layout",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/clubs/{clubLayoutId}")
    public ResponseEntity<?> getClubLayout(
            @Parameter(description = "ID of the club layout to retrieve", required = true)
            @PathVariable String clubLayoutId,
            @Parameter(description = "Whether to include enriched data in the response", required = false)
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedClubLayout(clubLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawClubLayout(clubLayoutId));
        }
    }

    // === ZONE  ===

    @Operation(
        summary = "Add zone layout",
        description = "Creates a new zone layout in a club"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Zone layout created successfully",
            content = @Content(schema = @Schema(implementation = RawZoneLayoutResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/zones")
    public ResponseEntity<RawZoneLayoutResponse> addZoneLayout(
            @Parameter(description = "Zone layout creation request", required = true)
            @RequestBody AddZoneLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addZoneLayout(request));
    }

    @Operation(
        summary = "Get zone layout",
        description = "Retrieves a specific zone layout, optionally with enriched data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved zone layout",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Zone layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<?> getZoneLayout(
            @Parameter(description = "ID of the zone layout to retrieve", required = true)
            @PathVariable String zoneLayoutId,
            @Parameter(description = "Whether to include enriched data in the response", required = false)
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedZoneLayout(zoneLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawZoneLayout(zoneLayoutId));
        }
    }

    @Operation(
        summary = "Update zone layout",
        description = "Updates an existing zone layout"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Zone layout updated successfully",
            content = @Content(schema = @Schema(implementation = RawZoneLayoutResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Zone layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PutMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<RawZoneLayoutResponse> updateZoneLayout(
            @Parameter(description = "ID of the zone layout to update", required = true)
            @PathVariable String zoneLayoutId,
            @Parameter(description = "Updated zone layout details", required = true)
            @RequestBody UpdateZoneLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateZoneLayout(zoneLayoutId, request));
    }

    @Operation(
        summary = "Delete zone layout",
        description = "Deletes a zone layout and its associated data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Zone layout deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Zone layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @DeleteMapping("/zones/{zoneLayoutId}")
    public ResponseEntity<Void> deleteZoneLayout(
            @Parameter(description = "ID of the zone layout to delete", required = true)
            @PathVariable String zoneLayoutId) {
        layoutService.deleteZoneLayout(zoneLayoutId);
        return ResponseEntity.noContent().build();
    }

    // === GROUP ===

    @Operation(
        summary = "Add station group layout",
        description = "Creates a new station group layout in a zone"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station group layout created successfully",
            content = @Content(schema = @Schema(implementation = RawStationGroupLayoutResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Zone layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/groups")
    public ResponseEntity<RawStationGroupLayoutResponse> addStationGroupLayout(
            @Parameter(description = "Station group layout creation request", required = true)
            @RequestBody AddStationGroupLayoutRequest request) {
        return ResponseEntity.ok(layoutService.addStationGroupLayout(request));
    }

    @Operation(
        summary = "Get station group layout",
        description = "Retrieves a specific station group layout, optionally with enriched data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved station group layout",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Station group layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<?> getStationGroupLayout(
            @Parameter(description = "ID of the station group layout to retrieve", required = true)
            @PathVariable String stationGroupLayoutId,
            @Parameter(description = "Whether to include enriched data in the response", required = false)
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedStationGroupLayout(stationGroupLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawStationGroupLayout(stationGroupLayoutId));
        }
    }

    @Operation(
        summary = "Update station group layout",
        description = "Updates an existing station group layout"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station group layout updated successfully",
            content = @Content(schema = @Schema(implementation = RawStationGroupLayoutResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Station group layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PutMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<RawStationGroupLayoutResponse> updateStationGroupLayout(
            @Parameter(description = "ID of the station group layout to update", required = true)
            @PathVariable String stationGroupLayoutId,
            @Parameter(description = "Updated station group layout details", required = true)
            @RequestBody UpdateStationGroupLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateStationGroupLayout(stationGroupLayoutId, request));
    }

    @Operation(
        summary = "Delete station group layout",
        description = "Deletes a station group layout and its associated data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Station group layout deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Station group layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @DeleteMapping("/groups/{stationGroupLayoutId}")
    public ResponseEntity<Void> deleteStationGroupLayout(
            @Parameter(description = "ID of the station group layout to delete", required = true)
            @PathVariable String stationGroupLayoutId) {
        layoutService.deleteStationGroupLayout(stationGroupLayoutId);
        return ResponseEntity.noContent().build();
    }

    // === STATION ===

    @Operation(
        summary = "Get station layout",
        description = "Retrieves a specific station layout, optionally with enriched data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved station layout",
            content = @Content(schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Station layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/stations/{stationLayoutId}")
    public ResponseEntity<?> getStationLayout(
            @Parameter(description = "ID of the station layout to retrieve", required = true)
            @PathVariable String stationLayoutId,
            @Parameter(description = "Whether to include enriched data in the response", required = false)
            @RequestParam(name = "enriched", defaultValue = "false") boolean enriched) {

        if (enriched) {
            return ResponseEntity.ok(layoutService.getEnrichedStationLayout(stationLayoutId));
        } else {
            return ResponseEntity.ok(layoutService.getRawStationLayout(stationLayoutId));
        }
    }

    @Operation(
        summary = "Update station layout",
        description = "Updates an existing station layout"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station layout updated successfully",
            content = @Content(schema = @Schema(implementation = RawStationLayoutResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Station layout not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PutMapping("/stations/{stationLayoutId}")
    public ResponseEntity<RawStationLayoutResponse> updateStationLayout(
            @Parameter(description = "ID of the station layout to update", required = true)
            @PathVariable String stationLayoutId,
            @Parameter(description = "Updated station layout details", required = true)
            @RequestBody UpdateStationLayoutRequest request) {
        return ResponseEntity.ok(layoutService.updateStationLayout(
                stationLayoutId, request));
    }
}
