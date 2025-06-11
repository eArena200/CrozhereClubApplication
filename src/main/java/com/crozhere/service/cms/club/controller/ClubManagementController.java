package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.ClubAddress;
import com.crozhere.service.cms.club.controller.model.GeoLocation;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.response.ClubDetailsResponse;
import com.crozhere.service.cms.club.controller.model.response.ClubResponse;
import com.crozhere.service.cms.club.repository.entity.Club;
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
@RequestMapping("/manage/clubs")
@Tag(name = "Club Management", description = "Admin-level APIs for creating and managing clubs")
public class ClubManagementController {

    private final ClubService clubService;

    @Autowired
    public ClubManagementController(
            ClubService clubService){
        this.clubService = clubService;
    }

    @Operation(summary = "CreateNewClub", description = "Creates a new club for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Club created",
                    content = @Content(schema = @Schema(implementation = ClubResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ClubResponse> createClub(
            @Parameter(description = "CreateClubRequest", required = true)
            @RequestBody CreateClubRequest request) {
        Club club = clubService.createClub(request);
        return ResponseEntity.status(201).body(getClubResponse(club));
    }

    @Operation(summary = "UpdateClubDetails", description = "Updates existing club details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Club updated",
                    content = @Content(schema = @Schema(implementation = ClubResponse.class))),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @PutMapping("/{clubId}")
    public ResponseEntity<ClubResponse> updateClub(
            @PathVariable Long clubId,
            @RequestBody UpdateClubRequest request) {
        Club club = clubService.updateClub(clubId, request);
        return ResponseEntity.ok(getClubResponse(club));
    }


    @Operation(summary = "GetClubDetailsByClubId", description = "Fetches club details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Club fetched",
                    content = @Content(schema = @Schema(implementation = ClubResponse.class))),
            @ApiResponse(responseCode = "404", description = "Club not found")
    })
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDetailsResponse> getClubDetailsById(
            @PathVariable Long clubId) {
        Club club = clubService.getClubById(clubId);
        return ResponseEntity.ok(getClubDetailsResponse(club));
    }


    @Operation(
            summary = "GetClubsForAdminId",
            description = "Retrieves a list of clubs for ClubAdminId"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved clubs",
                    content = @Content(schema = @Schema(implementation = ClubResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<ClubResponse>> getClubsForAdminId(
            @Parameter(description = "ClubAdminId", required = true)
            @RequestParam(value = "clubAdminId") Long clubAdminId){
        List<Club> clubs = clubService.getClubsByAdmin(clubAdminId);
        List<ClubResponse> clubsResponse = clubs.stream()
                .map(this::getClubResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(clubsResponse);
    }


    private ClubResponse getClubResponse(Club club) {
        return ClubResponse.builder()
                .clubId(club.getId())
                .clubName(club.getClubName())
                .clubAdminId(club.getClubAdmin().getId())
                .build();
    }

    private ClubDetailsResponse getClubDetailsResponse(Club club) {
        return ClubDetailsResponse.builder()
                .clubId((club.getId()))
                .clubName(club.getClubName())
                .clubAddress(ClubAddress.builder()
                        .streetAddress(club.getStreet())
                        .city(club.getCity())
                        .state(club.getState())
                        .pinCode(club.getPincode())
                        .geoLocation(GeoLocation.builder()
                                .latitude(club.getLatitude())
                                .longitude(club.getLongitude())
                                .build())
                        .build())
                .operatingHours(OperatingHours.builder()
                        .openTime(convertLocalTimeToString(club.getOpenTime()))
                        .closeTime(convertLocalTimeToString(club.getCloseTime()))
                        .build())
                .primaryContact(club.getPrimaryContact())
                .secondaryContact(club.getSecondaryContact())
                .build();
    }
}
