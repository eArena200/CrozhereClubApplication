package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.response.*;
import com.crozhere.service.cms.club.service.ClubService;
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

@Slf4j
@RestController
@RequestMapping(
        value = "/clubs",
        produces = "application/json"
)
@Tag(name = "Club", description = "APIs for fetching clubs and their stations")
public class ClubDetailsController {

    private final ClubService clubService;

    @Autowired
    public ClubDetailsController(
            ClubService clubService
    ){
        this.clubService = clubService;
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
        ClubDetailsResponse response = clubService.getDetailedClubById(clubId);
        return ResponseEntity.ok(response);
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
                clubService.getStationsByClubId(clubId);

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
        StationResponse response = clubService.getStationById(stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
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
    public ResponseEntity<List<RateCardResponse>> getAllRateCards(
            @Parameter(
                    name = "clubId",
                    description = "Id of the club for which rate-cards needs to be retrieved",
                    required = true
            )
            @PathVariable(value = "clubId") Long clubId
    ) {
        List<RateCardResponse> response = clubService.getRateCardsForClubId(clubId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
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
        RateCardDetailsResponse response = clubService.getRateCardDetailsById(rateCardId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
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
        List<RateResponse> response = clubService.getRatesForRateCard(rateCardId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
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
        RateResponse response = clubService.getRateById(rateId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
