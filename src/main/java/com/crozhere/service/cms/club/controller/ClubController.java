package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.controller.model.response.ClubResponse;
import com.crozhere.service.cms.club.controller.model.response.StationResponse;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
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

@Slf4j
@RestController
@RequestMapping("/clubs")
@Tag(name = "Club", description = "APIs for fetching clubs and their stations")
public class ClubController {

    private final ClubService clubService;

    @Autowired
    public ClubController(ClubService clubService){
        this.clubService = clubService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> addStation(
            @Parameter(description = "Station creation request containing station details", required = true)
            @RequestBody AddStationRequest addStationRequest){
        Station station = clubService.addStation(addStationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getStationResponse(station));
    }

    @Operation(
        summary = "Get station by ID",
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
    @GetMapping("/stations/{stationId}")
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
    @PutMapping("/stations/{stationId}")
    public ResponseEntity<StationResponse> updateStation(
            @Parameter(description = "ID of the station to update", required = true)
            @PathVariable("stationId") Long stationId,
            @Parameter(description = "Updated station details", required = true)
            @RequestBody UpdateStationRequest updateStationRequest){
        Station station =
                clubService.updateStation(stationId, updateStationRequest);
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
    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity<Void> deleteStation(
            @Parameter(description = "ID of the station to delete", required = true)
            @PathVariable Long stationId){
        clubService.deleteStation(stationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
        summary = "Get stations by club ID",
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
    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> getStationsByClubId(
            @Parameter(description = "ID of the club to get stations for", required = true)
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

    private ClubResponse getClubResponse(Club club) {
        return ClubResponse.builder()
                .clubId(club.getId())
                .clubName(club.getClubName())
                .clubAdminId(club.getClubAdmin().getId())
                .build();
    }

    private StationResponse getStationResponse(Station station) {
        return StationResponse.builder()
                .stationId(station.getId())
                .clubId(station.getClub().getId())
                .stationName(station.getStationName())
                .stationType(station.getStationType())
                .stationLayoutId(station.getStationLayoutId())
                .stationGroupLayoutId(station.getStationGroupLayoutId())
                .isActive(station.getIsActive())
                .build();
    }

}
