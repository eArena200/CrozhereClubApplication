package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.request.*;
import com.crozhere.service.cms.club.controller.model.response.*;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.common.security.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(
        value = "/manage/clubs",
        produces = "application/json"
)
@Tag(name = "Club Management", description = "APIs for creating and managing clubs")
public class ClubManagementController {

    private final ClubService clubService;

    @Autowired
    public ClubManagementController(
            ClubService clubService
    ){
        this.clubService = clubService;
    }

    // CLUB APIs
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "CreateNewClub", description = "Creates a new club for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Club created",
                    content = @Content(
                            schema = @Schema(implementation = ClubResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping(
            value = "/createClub",
            consumes = "application/json"
    )
    public ResponseEntity<ClubResponse> createClub(
            @Parameter(
                    description = "CreateClubRequest",
                    required = true
            )
            @RequestBody CreateClubRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        ClubResponse response = clubService.createClub(clubAdminId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "UpdateClubDetails", description = "Updates existing club details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Club updated",
                    content = @Content(
                            schema = @Schema(implementation = ClubResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Club not found",
                    content = @Content(
                            schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(
                            schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping(
            value = "/updateClub/{clubId}",
            consumes = "application/json"
    )
    public ResponseEntity<ClubResponse> updateClub(
            @Parameter(
                    name = "clubId",
                    description = "Id of club to be updated",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId,

            @Parameter(description = "UpdateClubRequest", required = true)
            @RequestBody UpdateClubDetailsRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        ClubResponse response =
                clubService.updateClubDetails(clubAdminId, clubId, request);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "GetClubsForAdminId",
            description = "Retrieves a list of clubs for ClubAdminId"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved clubs",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ClubResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping(value = "/getClubsForAdmin")
    public ResponseEntity<List<ClubResponse>> getClubsForAdminId(){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        List<ClubResponse> response = clubService.getClubsByAdminId(clubAdminId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "GetClubDetailsByClubId", description = "Fetches club details by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Club fetched",
                    content = @Content(schema = @Schema(implementation = ClubResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Club not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/getClubDetails/{clubId}")
    public ResponseEntity<ClubResponse> getClubDetailsById(
            @Parameter(
                    name = "clubId",
                    description = "ID of the club to retrieve",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ) {
        ClubResponse response = clubService.getClubById(clubId);
        return ResponseEntity.ok(response);
    }

    // STATION APIs
    @SecurityRequirement(name = "bearerAuth")
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
                    description = "Club not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/getStationsByClubId/{clubId}")
    public ResponseEntity<List<StationResponse>> getStationsByClubId(
            @Parameter(
                    name = "clubId",
                    description = "ClubId for which stations needs to be retrieved",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ){
        List<StationResponse> stationsResponse =
                clubService.getStationsByClubId(clubId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stationsResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
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
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Club not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping("/addStation")
    public ResponseEntity<StationResponse> addStation(
            @Parameter(description = "AddStationRequest", required = true)
            @RequestBody AddStationRequest addStationRequest
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        StationResponse response = clubService.addStation(clubAdminId, addStationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
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
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Station not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping(
            value = "updateStation/{stationId}",
            consumes = "application/json"
    )
    public ResponseEntity<StationResponse> updateStation(
            @Parameter(
                    name = "stationId",
                    description = "Id of station to be updated",
                    required = true
            )
            @PathVariable("stationId") Long stationId,

            @Parameter(description = "UpdateStationRequest", required = true)
            @RequestBody UpdateStationRequest updateStationRequest
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        StationResponse response =
                clubService.updateStationDetails(clubAdminId, stationId, updateStationRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
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
                    description = "Station not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping(
            value = "toggleStation/{stationId}",
            consumes = "application/json"
    )
    public ResponseEntity<StationResponse> toggleStationStatus(
            @Parameter(
                    name = "stationId",
                    description = "Id of the station to be toggled",
                    required = true
            )
            @PathVariable(value = "stationId") Long stationId
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        StationResponse response =
                clubService.toggleStationStatus(clubAdminId, stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
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
                    description = "Station not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping(value = "deleteStation/{stationId}")
    public ResponseEntity<Void> deleteStation(
            @Parameter(
                    name = "stationId",
                    description = "ID of the station to delete",
                    required = true
            )
            @PathVariable("stationId") Long stationId
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        clubService.softDeleteStation(clubAdminId, stationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    //RATE-CARD APIs
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get All Rate Cards By ClubId",
            description = "Fetch all rate cards for a club"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all rate cards",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RateCardDetailsResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Club not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/getRateCardsByClubId/{clubId}")
    public ResponseEntity<List<RateCardDetailsResponse>> getRateCardsByClubId(
            @Parameter(
                    name = "clubId",
                    description = "Id of the club for which rate-cards needs to be retrieved",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ) {
        List<RateCardDetailsResponse> response = clubService.getRateCardsForClubId(clubId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create Rate Card",
            description = "Creates a new rate card for the specified club"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rate card created successfully",
                    content = @Content(schema = @Schema(implementation = RateCardResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Club not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping("/createRateCard/{clubId}")
    public ResponseEntity<RateCardResponse> createRateCard(
            @Parameter(
                    name = "clubId",
                    description = "Id of the club for which Rate-card is created",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId,

            @Parameter(description = "CreateRateCardRequest", required = true)
            @RequestBody CreateRateCardRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateCardResponse response =
                clubService.createRateCard(clubAdminId, clubId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update Rate Card",
            description = "Updates an existing rate card"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rate card created successfully",
                    content = @Content(schema = @Schema(implementation = RateCardDetailsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate-card not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )

    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping("/updateRateCard/{rateCardId}")
    public ResponseEntity<RateCardResponse> updateRateCard(
            @Parameter(
                    name = "rateCardId",
                    description = "Id of the rate-card to be updated",
                    required = true
            )
            @PathVariable(value = "rateCardId") Long rateCardId,

            @Parameter(description = "UpdateRateCardRequest", required = true)
            @RequestBody UpdateRateCardDetailsRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateCardResponse response =
                clubService.updateRateCardDetails(clubAdminId, rateCardId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete Rate Card",
            description = "Delete a rate card by Id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully deleted rate-card"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate-card not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @DeleteMapping("/removeRateCard/{rateCardId}")
    public ResponseEntity<Void> deleteRateCard(
            @Parameter(
                    name="rateCardId",
                    description = "Id of the rate-card to remove",
                    required = true
            )
            @PathVariable(value = "rateCardId") Long rateCardId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        clubService.softDeleteRateCard(clubAdminId, rateCardId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    //RATE APIs
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Add Rate",
            description = "Add a rate to a specific rate card"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully added rate",
                    content = @Content(schema = @Schema(implementation = RateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate-card not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping(
            value = "/addRate/{rateCardId}",
            consumes = "application/json"
    )
    public ResponseEntity<RateResponse> addRate(
            @Parameter(
                    name = "rateCardId",
                    description = "Id of rate-card to which the rate will be added",
                    required = true
            )
            @PathVariable(value = "rateCardId") Long rateCardId,

            @Parameter(description = "AddRateRequest", required = true)
            @RequestBody AddRateRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateResponse response = clubService.addRate(clubAdminId, rateCardId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update Rate",
            description = "Update a specific rate"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated rate",
                    content = @Content(schema = @Schema(implementation = RateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping("/updateRate/{rateId}")
    public ResponseEntity<RateResponse> updateRate(
            @Parameter(
                    name = "rateId",
                    description = "Id of rate to be updated",
                    required = true
            )
            @PathVariable(value = "rateId") Long rateId,

            @Parameter(description = "UpdateRateRequest", required = true)
            @RequestBody UpdateRateDetailsRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateResponse response = clubService.updateRate(clubAdminId, rateId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete Rate",
            description = "Delete a specific rate"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully deleted rate"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping("/removeRate/{rateId}")
    public ResponseEntity<Void> deleteRate(
            @Parameter(
                    name = "rateId",
                    description = "Id of rate to be deleted",
                    required = true)
            @PathVariable(value = "rateId") Long rateId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        clubService.softDeleteRate(clubAdminId, rateId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Add Rate Charge",
            description = "Add a rate-charge to a rate"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully added rate-charge",
                    content = @Content(schema = @Schema(implementation = RateChargeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping(
            value = "/addRateCharge/{rateId}",
            consumes = "application/json"
    )
    public ResponseEntity<RateChargeResponse> addRateCharge(
            @Parameter(
                    name = "rateId",
                    description = "Id of rate to which the rate-charge will be added",
                    required = true
            )
            @PathVariable(value = "rateId") Long rateId,

            @Parameter(description = "AddRateChargeRequest", required = true)
            @RequestBody AddRateChargeRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateChargeResponse response = clubService.addRateCharge(clubAdminId, rateId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update Rate Charge",
            description = "Updates a rate-charge"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated rate-charge",
                    content = @Content(schema = @Schema(implementation = RateChargeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "RateCharge not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PutMapping(
            value = "/updateRateCharge/{rateChargeId}",
            consumes = "application/json"
    )
    public ResponseEntity<RateChargeResponse> updateRateCharge(
            @Parameter(
                    name = "rateChargeId",
                    description = "Id of rate-charge to be updated",
                    required = true
            )
            @PathVariable(value = "rateChargeId") Long rateChargeId,

            @Parameter(description = "UpdateRateChargeRequest", required = true)
            @RequestBody UpdateRateChargeRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateChargeResponse response = clubService.updateRateCharge(clubAdminId, rateChargeId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete Rate Charge",
            description = "Delete a specific rate-charge"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully deleted rate-charge"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rate-charge not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping("/removeRateCharge/{rateChargeId}")
    public ResponseEntity<Void> deleteRateCharge(
            @Parameter(
                    name = "rateChargeId",
                    description = "Id of rate-charge to be deleted",
                    required = true)
            @PathVariable(value = "rateChargeId") Long rateChargeId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        clubService.softDeleteRateCharge(clubAdminId, rateChargeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
