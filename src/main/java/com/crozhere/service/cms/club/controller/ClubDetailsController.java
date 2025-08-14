package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.GeoLocation;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.controller.model.response.*;
import com.crozhere.service.cms.club.repository.entity.*;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping(
        value = "/clubs",
        produces = "application/json"
)
@Tag(name = "Club", description = "APIs for fetching clubs and their stations")
public class ClubDetailsController {

    private final ClubService clubService;
    private final RateService rateService;

    @Autowired
    public ClubDetailsController(
            ClubService clubService,
            RateService rateService
    ){
        this.clubService = clubService;
        this.rateService = rateService;
    }

    // CLUB APIs
    @Operation(summary = "GetClubDetailsByClubId", description = "Fetches club details by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Club fetched",
                    content = @Content(schema = @Schema(implementation = ClubDetailsResponse.class))
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
    @GetMapping("/getClubDetails/{clubId}")
    public ResponseEntity<ClubDetailsResponse> getClubDetailsById(
            @Parameter(
                    name = "clubId",
                    description = "ID of the club to retrieve",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ) {
        Club club = clubService.getClubById(clubId);
        return ResponseEntity.ok(getClubDetailsResponse(club));
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
                    description = "Club not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @GetMapping("/getStationsForClub/{clubId}")
    public ResponseEntity<List<StationResponse>> getStationsByClubId(
            @Parameter(
                    name = "clubId",
                    description = "ClubId for which stations needs to be retrieved",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ){
        List<StationResponse> stationsResponse =
                clubService.getStationsByClubId(clubId)
                        .stream()
                        .map(this::getStationResponse)
                        .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stationsResponse);
    }

    // STATION APIs
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
                    description = "Station not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            )
    })
    @GetMapping("/getStationDetails/{stationId}")
    public ResponseEntity<StationResponse> getStation(
            @Parameter(
                    name = "stationId",
                    description = "ID of the station to retrieve",
                    required = true
            )
            @PathVariable(value = "stationId") Long stationId
    ){
        Station station = clubService.getStationById(stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
    }


    // RATE-CARD APIs
    @Operation(
            summary = "Get All Rate Cards",
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
    @GetMapping("/getRateCardsForClub/{clubId}")
    public ResponseEntity<List<RateCardDetailsResponse>> getAllRateCards(
            @Parameter(
                    name = "clubId",
                    description = "Id of the club for which rate-cards needs to be retrieved",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ) {
        List<RateCard> rateCards = rateService.getRateCardsForClubId(clubId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rateCards.stream()
                        .map(this::getRateCardDetailsResponse)
                        .toList());
    }


    @Operation(
            summary = "Get Rate Card Details",
            description = "Fetch a rate card by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved rate card",
                    content = @Content(schema = @Schema(implementation = RateCardDetailsResponse.class))
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
    @GetMapping("/getRateCard/{rateCardId}")
    public ResponseEntity<RateCardDetailsResponse> getRateCard(
            @Parameter(
                    name = "rateCardId",
                    description = "ID of the rate-card to retrieve",
                    required = true
            )
            @PathVariable(value = "rateCardId") Long rateCardId
    ) {
        RateCard rateCard = rateService.getRateCard(rateCardId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateCardDetailsResponse(rateCard));
    }


    // RATE LEVEL APIs
    @Operation(
            summary = "Get Rates For Rate Card",
            description = "Get all rates under a rate card"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched all rates for rate-card",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RateResponse.class)))
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
    @GetMapping("/getRatesInRateCard/{rateCardId}")
    public ResponseEntity<List<RateResponse>> getRatesForRateCard(
            @Parameter(
                    name = "rateCardId",
                    description = "ID of the rate-card for which rates needs to be retrieved",
                    required = true
            )
            @PathVariable(value = "rateCardId") Long rateCardId
    ) {
        List<Rate> rates = rateService.getRatesForRateCard(rateCardId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rates.stream()
                        .map(this::getRateResponse)
                        .toList());
    }

    @Operation(
            summary = "Get Rate Details",
            description = "Get a specific rate by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched rate",
                    content = @Content(schema = @Schema(implementation = RateResponse.class))
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
    @GetMapping("/getRate/{rateId}")
    public ResponseEntity<RateResponse> getRate(
            @Parameter(
                    name = "rateId",
                    description = "ID of the rate to be retrieved",
                    required = true
            )
            @PathVariable(value = "rateId") Long rateId
    ) {
        Rate rate = rateService.getRate(rateId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateResponse(rate));
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
                .isActive(station.getIsActive())
                .isLive(station.getIsLive())
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
