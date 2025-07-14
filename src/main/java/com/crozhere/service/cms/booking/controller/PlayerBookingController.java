package com.crozhere.service.cms.booking.controller;

import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingDetailsResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingIntentDetailsResponse;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/booking/player")
@Tag(
        name = "Player Bookings",
        description = "APIs for players to manage their bookings and booking intents"
)
public class PlayerBookingController {

    private final BookingService bookingService;

    @Autowired
    public PlayerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create booking intent for player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking intent created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping("/createBookingIntent")
    public ResponseEntity<BookingIntentDetailsResponse> createBookingIntentForPlayer(
            @Parameter(
                    description = "Booking intent creation request",
                    required = true
            )
            @Valid
            @RequestBody
            CreatePlayerBookingIntentRequest request
    ) {
        BookingIntentDetailsResponse response =
                bookingService.createBookingIntentForPlayer(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get active booking intents for player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fetched active intents"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/getActiveIntents/{playerId}")
    public ResponseEntity<List<BookingIntentDetailsResponse>> getActiveIntentsForPlayer(
            @Parameter(description = "Player ID", required = true)
            @PathVariable("playerId") Long playerId
    ) {
        List<BookingIntentDetailsResponse> activeIntents =
                bookingService.getActiveIntentsForPlayer(playerId);
        return ResponseEntity.ok(activeIntents);
    }

    @Operation(summary = "Cancel player booking intent")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking intent cancelled"),
            @ApiResponse(responseCode = "400", description = "Already cancelled or invalid", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/cancelBookingIntent/{playerId}/{intentId}")
    public ResponseEntity<Void> cancelBookingIntentForPlayer(
            @Parameter(description = "Player ID", required = true)
            @PathVariable("playerId") Long playerId,
            @Parameter(description = "Booking Intent ID", required = true)
            @PathVariable("intentId") Long intentId
    ) {
        bookingService.cancelPlayerBookingIntent(playerId, intentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get booking by intent ID for player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/getBookingByIntentId/{playerId}/{intentId}")
    public ResponseEntity<BookingDetailsResponse> getBookingByIntentId(
            @Parameter(description = "Player ID", required = true)
            @PathVariable("playerId") Long playerId,
            @Parameter(description = "Booking Intent ID", required = true)
            @PathVariable("intentId") Long intentId
    ) {
        BookingDetailsResponse response =
                bookingService.getPlayerBookingByIntentId(playerId, intentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get booking details by booking ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/getBookingDetails/{playerId}/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBookingDetails(
            @Parameter(description = "Player ID", required = true)
            @PathVariable("playerId") Long playerId,
            @Parameter(description = "Booking ID", required = true)
            @PathVariable("bookingId") Long bookingId
    ) {
        BookingDetailsResponse response =
                bookingService.getPlayerBookingById(playerId, bookingId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cancel a booking by booking ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid or already cancelled", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping("/cancelBooking/{playerId}/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @Parameter(description = "Player ID", required = true)
            @PathVariable("playerId") Long playerId,
            @Parameter(description = "Booking ID", required = true)
            @PathVariable("bookingId") Long bookingId
    ) {
        bookingService.cancelPlayerBooking(playerId, bookingId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List all bookings for player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings fetched"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/getBookings/{playerId}")
    public ResponseEntity<List<BookingDetailsResponse>> listBookingsForPlayer(
            @Parameter(description = "Player ID", required = true)
            @PathVariable("playerId") Long playerId
    ) {
        List<BookingDetailsResponse> bookings = bookingService.listBookingByPlayerId(playerId);
        return ResponseEntity.ok(bookings);
    }
}