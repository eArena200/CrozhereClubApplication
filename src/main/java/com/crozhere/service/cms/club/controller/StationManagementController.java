package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.controller.model.response.StationResponse;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertLocalTimeToString;

@Slf4j
@RestController
@RequestMapping("/manage/stations")
@Tag(name = "Station Management", description = "APIs for creating and managing stations")
public class StationManagementController {

    private final ClubService clubService;

    @Autowired
    public StationManagementController(
            ClubService clubService) {
        this.clubService = clubService;
    }


    @Operation(
            summary = "Add New Station",
            description = "Creates a new station in a club with the provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Station created successfully",
                    content = @Content(schema = @Schema(implementation = StationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Club not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    public ResponseEntity<StationResponse> addStation(
            @Parameter(description = "AddStationRequest", required = true)
            @RequestBody AddStationRequest addStationRequest){
        log.info("AddStationRequest: {}", addStationRequest.toString());
        Station station = clubService.addStation(addStationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getStationResponse(station));
    }



    @Operation(
            summary = "Get Station by StationId",
            description = "Retrieves a specific station by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved station",
                    content = @Content(schema = @Schema(implementation = StationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Station not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{stationId}")
    public ResponseEntity<StationResponse> getStation(
            @Parameter(description = "ID of the station to retrieve", required = true)
            @PathVariable("stationId") Long stationId){

        Station station = clubService.getStation(stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
    }


    @Operation(
            summary = "Update station details",
            description = "Updates the details of an existing station"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Station updated successfully",
                    content = @Content(schema = @Schema(implementation = StationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Station not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PutMapping("/{stationId}")
    public ResponseEntity<StationResponse> updateStation(
            @Parameter(description = "StationId", required = true)
            @PathVariable("stationId") Long stationId,
            @Parameter(description = "UpdateStationRequest", required = true)
            @RequestBody UpdateStationRequest updateStationRequest){
        Station station =
                clubService.updateStation(stationId, updateStationRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
    }

    @Operation(
            summary = "Toggle station status",
            description = "Updates the details of an existing station"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Station updated successfully",
                    content = @Content(schema = @Schema(implementation = StationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Station not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PutMapping("/{stationId}/toggle")
    public ResponseEntity<StationResponse> toggleStationStatus(
            @Parameter(description = "StationId", required = true)
            @PathVariable("stationId") Long stationId){
        Station station =
                clubService.toggleStationStatus(stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
    }


    @Operation(
            summary = "Delete a station",
            description = "Deletes a station from a club"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Station deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Station not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @DeleteMapping("/{stationId}")
    public ResponseEntity<Void> deleteStation(
            @Parameter(description = "ID of the station to delete", required = true)
            @PathVariable Long stationId){
        log.info("Delete Station request for stationId: {}", stationId);
        clubService.deleteStation(stationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    @Operation(
            summary = "Get stations for a club",
            description = "Retrieves all stations belonging to a specific club"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved stations",
                    content = @Content(schema = @Schema(implementation = StationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Club not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<StationResponse>> getStationsByClubId(
            @Parameter(description = "ClubId", required = true)
            @RequestParam(value = "clubId") Long clubId){
        List<StationResponse> stationsResponse =
                clubService.getStationsByClubId(clubId)
                        .stream()
                        .map(this::getStationResponse)
                        .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stationsResponse);
    }


    private StationResponse getStationResponse(Station station) {
        return StationResponse.builder()
                .stationId(station.getId())
                .clubId(station.getClub().getId())
                .stationName(station.getStationName())
                .stationType(station.getStationType())
                .operatingHours(OperatingHours.builder()
                        .openTime(convertLocalTimeToString(station.getOpenTime()))
                        .closeTime(convertLocalTimeToString(station.getCloseTime()))
                        .build())
                .rateId(station.getRate().getId())
                .rateName(station.getRate().getName())
                .capacity(station.getCapacity())
                .isActive(station.getIsActive())
                .build();
    }
}
