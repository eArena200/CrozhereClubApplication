package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.request.*;
import com.crozhere.service.cms.club.controller.model.response.ChargeResponse;
import com.crozhere.service.cms.club.controller.model.response.RateCardDetailsResponse;
import com.crozhere.service.cms.club.controller.model.response.RateCardResponse;
import com.crozhere.service.cms.club.controller.model.response.RateResponse;
import com.crozhere.service.cms.club.repository.entity.RateCharge;
import com.crozhere.service.cms.club.repository.entity.Rate;
import com.crozhere.service.cms.club.repository.entity.RateCard;
import com.crozhere.service.cms.club.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertLocalTimeToString;

@RestController
@RequestMapping("/manage/clubs/{clubId}/rate-cards")
@Tag(name = "Rate Management", description = "APIs for managing rate cards and rates")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;

    // RATE-CARD LEVEL APIs
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
    })
    @PostMapping
    public ResponseEntity<RateCardResponse> createRateCard(
            @PathVariable Long clubId,
            @RequestBody CreateRateCardRequest request) {
        RateCard rateCard = rateService.createRateCard(clubId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getRateCardResponse(rateCard));
    }


    @Operation(
            summary = "Update Rate Card",
            description = "Updates an existing rate card"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rate card created successfully",
                    content = @Content(schema = @Schema(implementation = RateCardDetailsResponse.class))
            )
    })
    @PutMapping("/{rateCardId}")
    public ResponseEntity<RateCardDetailsResponse> updateRateCard(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId,
            @RequestBody UpdateRateCardRequest request) {
        RateCard rateCard = rateService.updateRateCard(rateCardId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateCardDetailsResponse(rateCard));
    }


    @Operation(
            summary = "Get Rate Card",
            description = "Fetch a rate card by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved rate card",
                    content = @Content(schema = @Schema(implementation = RateCardDetailsResponse.class))
            )
    })
    @GetMapping("/{rateCardId}")
    public ResponseEntity<RateCardDetailsResponse> getRateCard(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId) {
        RateCard rateCard = rateService.getRateCard(rateCardId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateCardDetailsResponse(rateCard));
    }

    @Operation(
            summary = "Get All Rate Cards",
            description = "Fetch all rate cards for a club"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all rate cards",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RateCardResponse.class)))
            ),
    })
    @GetMapping
    public ResponseEntity<List<RateCardResponse>> getAllRateCards(
            @PathVariable Long clubId) {
        List<RateCard> rateCards = rateService.getRateCardsForClubId(clubId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rateCards.stream()
                        .map(this::getRateCardResponse)
                        .toList());
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
    })
    @DeleteMapping("/{rateCardId}")
    public ResponseEntity<Void> deleteRateCard(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId) {
        rateService.deleteRateCard(rateCardId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // RATE LEVEL APIs
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
    })
    @PostMapping("/{rateCardId}/rates")
    public ResponseEntity<RateResponse> addRate(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId,
            @RequestBody AddRateRequest request) {
        Rate rate = rateService.addRate(rateCardId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getRateResponse(rate));
    }

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
    })
    @GetMapping("/{rateCardId}/rates")
    public ResponseEntity<List<RateResponse>> getRatesForRateCard(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId) {
        List<Rate> rates = rateService.getRatesForRateCard(rateCardId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rates.stream()
                        .map(this::getRateResponse)
                        .toList());
    }

    @Operation(
            summary = "Get Rate",
            description = "Get a specific rate by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched rate",
                    content = @Content(schema = @Schema(implementation = RateResponse.class))
            ),
    })
    @GetMapping("/{rateCardId}/rates/{rateId}")
    public ResponseEntity<RateResponse> getRate(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId,
            @PathVariable Long rateId) {
        Rate rate = rateService.getRate(rateId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateResponse(rate));
    }

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
    })
    @PutMapping("/{rateCardId}/rates/{rateId}")
    public ResponseEntity<RateResponse> updateRate(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId,
            @PathVariable Long rateId,
            @RequestBody UpdateRateRequest request) {
        Rate rate = rateService.updateRate(rateId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getRateResponse(rate));
    }

    @Operation(
            summary = "Delete Rate",
            description = "Delete a specific rate"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully deleted rate"
            ),
    })
    @DeleteMapping("/{rateCardId}/rates/{rateId}")
    public ResponseEntity<Void> deleteRate(
            @PathVariable Long clubId,
            @PathVariable Long rateCardId,
            @PathVariable Long rateId) {
        rateService.deleteRate(rateId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
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
