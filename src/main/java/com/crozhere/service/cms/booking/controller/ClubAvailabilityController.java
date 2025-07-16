package com.crozhere.service.cms.booking.controller;

import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByStationRequest;
import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByTimeRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/club/availability")
@Tag(name = "Club Availability", description = "APIs for clubs to check station availability by time or station")
public class ClubAvailabilityController {

    private final BookingService bookingService;

    @Operation(
            summary = "Check station availability by time",
            description = "Returns availability of all stations for a club and station type within a specific time window"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BookingAvailabilityByTimeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/by-time")
    public ResponseEntity<BookingAvailabilityByTimeResponse> checkAvailabilityByTime(
            @Parameter(
                    description = "Request with clubId, stationType, startTime, endTime",
                    required = true
            )
            @Valid
            @RequestBody
            BookingAvailabilityByTimeRequest bookingAvailabilityByTimeRequest
    ) {
        log.info("Received BY_TIME request: {}", bookingAvailabilityByTimeRequest.toString());
        BookingAvailabilityByTimeResponse response =
                bookingService.checkAvailabilityByTime(bookingAvailabilityByTimeRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Check station availability by station",
            description = "Returns availability of specific stations in a club within a specific time window"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BookingAvailabilityByStationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/by-station")
    public ResponseEntity<BookingAvailabilityByStationResponse> checkAvailabilityByStations(
            @Parameter(
                    description = "Request with clubId, stationIds, startTime, endTime",
                    required = true
            )
            @Valid
            @RequestBody
            BookingAvailabilityByStationRequest bookingAvailabilityByStationRequest
    ) throws BookingServiceException {
        BookingAvailabilityByStationResponse response =
                bookingService.checkAvailabilityByStations(bookingAvailabilityByStationRequest);
        return ResponseEntity.ok(response);
    }
}
