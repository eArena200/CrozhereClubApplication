package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.GeoLocation;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.controller.model.request.*;
import com.crozhere.service.cms.club.controller.model.response.*;
import com.crozhere.service.cms.club.repository.entity.*;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.RateService;
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

import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertLocalTimeToString;

@Slf4j
@RestController
@RequestMapping(
        value = "/manage/clubs",
        produces = "application/json"
)
@Tag(name = "Club Management", description = "APIs for creating and managing clubs")
public class ClubManagementController {

    private final ClubService clubService;
    private final RateService rateService;

    @Autowired
    public ClubManagementController(
            ClubService clubService,
            RateService rateService
    ){
        this.clubService = clubService;
        this.rateService = rateService;
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
        Club club = clubService.createClub(clubAdminId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(getClubResponse(club));
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "UpdateClubDetails", description = "Updates existing club details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Club updated",
                    content = @Content(
                            schema = @Schema(implementation = ClubDetailsResponse.class))
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
    public ResponseEntity<ClubDetailsResponse> updateClub(
            @Parameter(
                    name = "ClubId",
                    description = "Id of club to be updated",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId,

            @Parameter(description = "UpdateClubRequest", required = true)
            @RequestBody UpdateClubRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        Club club = clubService.updateClub(clubAdminId, clubId, request);
        return ResponseEntity.ok(getClubDetailsResponse(club));
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
        List<Club> clubs = clubService.getClubsByAdmin(clubAdminId);
        List<ClubResponse> clubsResponse = clubs.stream()
                .map(this::getClubResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(clubsResponse);
    }

    // STATION APIs
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
        Station station = clubService.addStation(clubAdminId, addStationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getStationResponse(station));
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
                    name = "StationId",
                    description = "Id of station to be updated",
                    required = true
            )
            @PathVariable("stationId") Long stationId,

            @Parameter(description = "UpdateStationRequest", required = true)
            @RequestBody UpdateStationRequest updateStationRequest
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        Station station =
                clubService.updateStation(clubAdminId, stationId, updateStationRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
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
                    name = "StationId",
                    description = "Id of the station to be toggled",
                    required = true
            )
            @PathVariable(value = "stationId") Long stationId
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        Station station =
                clubService.toggleStationStatus(clubAdminId, stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
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
                    name = "StationId",
                    description = "ID of the station to delete",
                    required = true
            )
            @PathVariable("stationId") Long stationId
    ){
        Long clubAdminId = AuthUtil.getRoleBasedId();
        clubService.deleteStation(clubAdminId, stationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    //RATE-CARD APIs
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
        RateCard rateCard =
                rateService.createRateCard(clubAdminId, clubId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getRateCardResponse(rateCard));
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
    public ResponseEntity<RateCardDetailsResponse> updateRateCard(
            @Parameter(
                    name = "rateCardId",
                    description = "Id of the rate-card to be updated",
                    required = true
            )
            @PathVariable(value = "rateCardId") Long rateCardId,

            @Parameter(description = "UpdateRateCardRequest", required = true)
            @RequestBody UpdateRateCardRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        RateCard rateCard =
                rateService.updateRateCard(clubAdminId, rateCardId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateCardDetailsResponse(rateCard));
    }

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
        rateService.deleteRateCard(clubAdminId, rateCardId);
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
        Rate rate = rateService.addRate(clubAdminId, rateCardId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getRateResponse(rate));
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
            @RequestBody UpdateRateRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        Rate rate = rateService.updateRate(clubAdminId, rateId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateResponse(rate));
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
        rateService.deleteRate(clubAdminId, rateId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    private ClubResponse getClubResponse(Club club) {
        return ClubResponse.builder()
                .clubId(club.getId())
                .clubName(club.getClubName())
                .clubAdminId(club.getClubAdminId())
                .build();
    }

    private ClubDetailsResponse getClubDetailsResponse(Club club) {
        return ClubDetailsResponse.builder()
                .clubId((club.getId()))
                .clubName(club.getClubName())
                .clubAddressDetails(ClubAddressDetails.builder()
                        .streetAddress(club.getClubAddress().getStreet())
                        .city(club.getClubAddress().getCity())
                        .state(club.getClubAddress().getState())
                        .pinCode(club.getClubAddress().getPincode())
                        .geoLocation(GeoLocation.builder()
                                .latitude(club.getClubAddress().getLatitude())
                                .longitude(club.getClubAddress().getLongitude())
                                .build())
                        .build())
                .operatingHours(OperatingHours.builder()
                        .openTime(convertLocalTimeToString(club.getClubOperatingHours().getOpenTime()))
                        .closeTime(convertLocalTimeToString(club.getClubOperatingHours().getCloseTime()))
                        .build())
                .primaryContact(club.getClubContact().getPrimaryContact())
                .secondaryContact(club.getClubContact().getSecondaryContact())
                .build();
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
                .isLive(station.getIsLive())
                .isActive(station.getIsActive())
                .build();
    }

    private RateCardResponse getRateCardResponse(RateCard rateCard) {
        return RateCardResponse.builder()
                .rateCardId(rateCard.getId())
                .clubId(rateCard.getClub().getId())
                .name(rateCard.getName())
                .build();
    }

    private RateCardDetailsResponse getRateCardDetailsResponse(RateCard rateCard) {
        return RateCardDetailsResponse.builder()
                .rateCardId(rateCard.getId())
                .clubId(rateCard.getClub().getId())
                .name(rateCard.getName())
                .rateList(rateCard.getRates()
                        .stream()
                        .map(this::getRateResponse)
                        .toList())
                .build();
    }

    private RateResponse getRateResponse(Rate rate){
        return RateResponse.builder()
                .rateId(rate.getId())
                .rateCardId(rate.getRateCard().getId())
                .name(rate.getName())
                .charges(rate.getRateCharges()
                        .stream()
                        .map(this::getChargeResponse)
                        .toList())
                .build();
    }

    private ChargeResponse getChargeResponse(RateCharge rateCharge){
        return ChargeResponse.builder()
                .chargeId(rateCharge.getId())
                .rateId(rateCharge.getRate().getId())
                .chargeType(rateCharge.getChargeType())
                .chargeUnit(rateCharge.getUnit())
                .amount(rateCharge.getAmount())
                .minPlayers(rateCharge.getMinPlayers())
                .maxPlayers(rateCharge.getMaxPlayers())
                .startTime(convertLocalTimeToString(rateCharge.getStartTime()))
                .endTime(convertLocalTimeToString(rateCharge.getEndTime()))
                .build();
    }
}
