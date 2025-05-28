package com.crozhere.service.cms.booking.controller;


import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByStationRequest;
import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByTimeRequest;
import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingResponse;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.club.repository.entity.Station;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/booking")
@Tag(name = "Booking Management", description = "APIs for managing bookings, checking availability, and handling booking operations")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @Operation(
        summary = "Create a new booking",
        description = "Creates a new booking for specified stations with player details and time slot"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Booking created successfully",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters or booking not available"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Player or station not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Parameter(description = "Booking creation request containing player, station, and time details", required = true)
            @RequestBody CreateBookingRequest createBookingRequest){
        Booking booking = bookingService.createBooking(createBookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(getBookingResponse(booking));
    }

    @Operation(
        summary = "Get booking by ID",
        description = "Retrieves a specific booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved booking",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @Parameter(description = "ID of the booking to retrieve", required = true)
            @PathVariable("bookingId") Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(getBookingResponse(booking));
    }

    @Operation(
        summary = "Cancel a booking",
        description = "Cancels an existing booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking cancelled successfully",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Booking cannot be cancelled (e.g., already cancelled or past booking time)"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @Parameter(description = "ID of the booking to cancel", required = true)
            @PathVariable("bookingId") Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(getBookingResponse(booking));
    }

    @Operation(
        summary = "Get bookings by player ID",
        description = "Retrieves all bookings for a specific player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved player bookings",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Player not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<BookingResponse>> listBookingsByPlayer(
            @Parameter(description = "ID of the player to get bookings for", required = true)
            @PathVariable("playerId") Long playerId) {
        List<Booking> bookings = bookingService.listBookingByPlayerId(playerId);
        List<BookingResponse> responses = bookings.stream()
                .map(this::getBookingResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get bookings by club ID",
        description = "Retrieves all bookings for a specific club"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved club bookings",
            content = @Content(schema = @Schema(implementation = BookingResponse.class))
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
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<BookingResponse>> listBookingsByClub(
            @Parameter(description = "ID of the club to get bookings for", required = true)
            @PathVariable("clubId") Long clubId) {
        List<Booking> bookings = bookingService.listBookingByClubId(clubId);
        List<BookingResponse> responses = bookings.stream()
                .map(this::getBookingResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }


    @Operation(
        summary = "Check booking availability by time",
        description = "Checks availability of stations for a given time window"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully checked availability",
            content = @Content(schema = @Schema(implementation = BookingAvailabilityByTimeResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
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
    @PostMapping("/availability/by-time")
    public ResponseEntity<BookingAvailabilityByTimeResponse> checkAvailabilityByTime(
            @Parameter(description = "Request containing club, station type, and time window details", required = true)
            @Valid @RequestBody BookingAvailabilityByTimeRequest request){
        BookingAvailabilityByTimeResponse response =
                bookingService.checkAvailabilityByTime(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Check booking availability by station",
        description = "Checks availability of specific stations for a given time window"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully checked availability",
            content = @Content(schema = @Schema(implementation = BookingAvailabilityByStationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club or stations not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @PostMapping("/availability/by-station")
    public ResponseEntity<BookingAvailabilityByStationResponse> checkAvailabilityByStation(
            @Parameter(description = "Request containing club, specific stations, and time window details", required = true)
            @Valid @RequestBody BookingAvailabilityByStationRequest request){
        BookingAvailabilityByStationResponse response =
                bookingService.checkAvailabilityByStations(request);
        return ResponseEntity.ok(response);
    }



    private BookingResponse getBookingResponse(Booking booking){
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .playerId(booking.getPlayer().getId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .players(booking.getPlayersCount())
                .stationIds(booking.getStations().stream().map(Station::getId).toList())
                .build();
    }
}
