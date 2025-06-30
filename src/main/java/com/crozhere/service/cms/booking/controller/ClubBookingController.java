package com.crozhere.service.cms.booking.controller;

import com.crozhere.service.cms.booking.controller.model.request.CreateBookingIntentRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingIntentResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingResponse;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;
import com.crozhere.service.cms.user.repository.entity.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/manage/clubs/{clubId}/bookings")
@Tag(
        name = "Club Booking Management",
        description = "APIs for clubs to manage their bookings and booking intents"
)
public class ClubBookingController {

    private final BookingService bookingService;

    @Autowired
    public ClubBookingController(
            BookingService bookingService){
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "CreateBookingIntentForClub",
            description = "Creates a booking intent by a club admin for their club"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking intent created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/intents")
    public ResponseEntity<BookingIntentResponse> createBookingIntent(
            @Parameter(
                    description = "ID of the club creating the booking intent",
                    required = true
            )
            @PathVariable Long clubId,
            @Parameter(
                    description = "Booking intent creation request body",
                    required = true
            )
            @RequestBody CreateBookingIntentRequest request
    ) {
        try {
            request.setClubId(clubId);
            BookingIntent intent = bookingService.createBookingIntent(UserRole.CLUB_ADMIN, request);
            return ResponseEntity.ok(toBookingIntentResponse(intent));
        } catch (InvalidRequestException | BookingServiceException e) {
            log.error("Error creating booking intent", e);
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "GetBookingIntentDetails",
            description = "Fetches details of a booking intent by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking intent fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Booking intent not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/intents/{intentId}")
    public ResponseEntity<BookingIntentResponse> getBookingIntent(
            @Parameter(
                    description = "ID of the booking intent to retrieve",
                    required = true
            )
            @PathVariable Long intentId
    ) {
        BookingIntent intent = bookingService.getBookingIntentById(intentId);
        return ResponseEntity.ok(toBookingIntentResponse(intent));
    }

    @Operation(
            summary = "ListBookingsForClub",
            description = "Retrieves all bookings for a given club"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Club not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<BookingResponse>> listAllBookingsForClub(
            @Parameter(
                    description = "ID of the club",
                    required = true
            )
            @PathVariable Long clubId
    ) {
        List<Booking> bookings = bookingService.listBookingByClubId(clubId);
        List<BookingResponse> responses = bookings.stream()
                .map(this::toBookingResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "GetBookingDetailsForClub",
            description = "Retrieve a booking for a club by booking ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingsForClub(
            @Parameter(
                    description = "ID of the club",
                    required = true
            )
            @PathVariable Long clubId,
            @Parameter(
                    description = "ID of the booking to retrieve",
                    required = true
            )
            @PathVariable Long bookingId
    ) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(toBookingResponse(booking));
    }

    @Operation(
            summary = "Cancel a booking for a club",
            description = "Cancel a booking by its ID for a specific club"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or booking cannot be cancelled"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBookingForClub(
            @Parameter(
                    description = "ID of the club",
                    required = true
            )
            @PathVariable Long clubId,
            @Parameter(
                    description = "ID of the booking to cancel",
                    required = true
            )
            @PathVariable Long bookingId
    ) {
        Booking cancelled = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(toBookingResponse(cancelled));
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .clubId(booking.getClubId())
                .playerId(booking.getPlayerId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .players(booking.getPlayersCount())
                .stationType(booking.getStationType())
                .stationIds(booking.getStationIds())
                .build();
    }

    private BookingIntentResponse toBookingIntentResponse(BookingIntent intent) {
        return BookingIntentResponse.builder()
                .intentId(intent.getId())
                .clubId(intent.getClubId())
                .playerId(intent.getPlayerId())
                .stationType(intent.getStationType())
                .stationIds(intent.getStationIds())
                .players(intent.getPlayerCount())
                .startTime(intent.getStartTime())
                .endTime(intent.getEndTime())
                .expiresAt(intent.getExpiresAt())
                .totalCost(intent.getTotalCost())
                .isConfirmed(intent.isConfirmed())
                .build();
    }
}
