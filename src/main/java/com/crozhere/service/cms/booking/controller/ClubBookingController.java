package com.crozhere.service.cms.booking.controller;

import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.controller.model.response.BookingIntentDetailsResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingDetailsResponse;
import com.crozhere.service.cms.booking.controller.model.response.DashBoardStationStatusResponse;
import com.crozhere.service.cms.booking.controller.model.response.PaginatedListBookingsResponse;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.club.repository.entity.StationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/booking/club")
@Tag(
        name = "Club Bookings",
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
    @PostMapping("/createBookingIntent")
    public ResponseEntity<BookingIntentDetailsResponse> createBookingIntent(
            @Parameter(
                    description = "Booking intent creation request body",
                    required = true
            )
            @Valid
            @RequestBody
            CreateClubBookingIntentRequest request
    ) {
        BookingIntentDetailsResponse response =
                bookingService.createBookingIntentForClub(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List active booking intents for a club",
            description = "Returns all currently active (not expired, not confirmed, not cancelled) booking intents for a club"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active booking intents fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getActiveIntents/{clubId}")
    public ResponseEntity<List<BookingIntentDetailsResponse>> getActiveIntents(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId
    ) {
        List<BookingIntentDetailsResponse> activeIntents =
                bookingService.getActiveIntentsForClub(clubId);
        return ResponseEntity.ok(activeIntents);
    }

    @Operation(
            summary = "Cancel a booking intent",
            description = "Cancels a booking intent before it is confirmed or expires"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking intent cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Booking intent not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or intent already cancelled"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/cancelBookingIntent/{clubId}/{intentId}")
    public ResponseEntity<BookingIntentDetailsResponse> cancelBookingIntent(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId,
            @Parameter(description = "Intent ID", required = true)
            @PathVariable("intentId") Long intentId
    ) {
        bookingService.cancelClubBookingIntent(clubId, intentId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "ListBookingsForClub",
            description = "Retrieves all bookings for a given club with optional filters and pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Club not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/getBookings/{clubId}")
    public ResponseEntity<PaginatedListBookingsResponse> listBookingsForClub(
            @Parameter(description = "ID of the club", required = true)
            @PathVariable(name = "clubId") Long clubId,

            @Parameter(
                    description = "Request body containing filters for bookings (all fields optional).",
                    required = false
            )

            @Valid
            @RequestBody(required = false)
            ClubBookingsListFilterRequest filterRequest,

            @Parameter(
                    description = "Page number to fetch (1‑based). Defaults to 1.",
                    example = "1"
            )
            @RequestParam(defaultValue = "1")
            Integer page,

            @Parameter(
                    description = "Number of bookings per page. Maximum allowed is 100. Defaults to 10.",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            Integer pageSize
    ) {
        log.info("Received ListBookingsForClub FilterRequest: {}", filterRequest);
        final int MAX_PAGE_SIZE = 100;
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;
        if (page < 1) page = 1;

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (filterRequest == null) filterRequest = new ClubBookingsListFilterRequest();

        Page<BookingDetailsResponse> bookingPage =
                bookingService.listBookingByClubIdWithFilters(clubId, filterRequest, pageable);

        PaginatedListBookingsResponse paginatedResponse = PaginatedListBookingsResponse.builder()
                .bookings(bookingPage.getContent())
                .totalCount(bookingPage.getTotalElements())
                .build();

        return ResponseEntity.ok(paginatedResponse);
    }

    @Operation(
            summary = "GetBookingByBookingIntentId",
            description = "Fetches booking details by booking intent ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getBookingByIntentId/{clubId}/{intentId}")
    public ResponseEntity<BookingDetailsResponse> getBookingByIntentId(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId,
            @Parameter(description = "Intent ID", required = true)
            @PathVariable("intentId") Long intentId
    ) {
        BookingDetailsResponse response =
                bookingService.getClubBookingByIntentId(clubId, intentId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "GetBookingDetails",
            description = "Retrieve a booking for a club by booking ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getBookingDetails/{clubId}/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBookingsForClub(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId,
            @Parameter(description = "Intent ID", required = true)
            @PathVariable("bookingId") Long bookingId
    ) {
        BookingDetailsResponse response = bookingService.getClubBookingById(
                clubId, bookingId);
        return ResponseEntity.ok(response);
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
    @PutMapping("/cancelBooking/{clubId}/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> cancelBookingForClub(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId,
            @Parameter(description = "Intent ID", required = true)
            @PathVariable("bookingId") Long bookingId
    ) {
        bookingService.cancelClubBooking(clubId, bookingId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get dashboard station statuses for a club",
            description = "Returns current and upcoming booking info for each station in a club"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Station statuses fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/dashboardStatus/{clubId}")
    public ResponseEntity<Map<Long, DashBoardStationStatusResponse>> getDashboardStationStatus(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId
    ) {
        Map<Long, DashBoardStationStatusResponse> response =
                bookingService.getDashboardStationStatusDetailsForClub(clubId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List upcoming bookings for a club",
            description = "Returns upcoming confirmed bookings for the club within a given time window (default 12 hours)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming bookings fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/upcomingBookings/{clubId}")
    public ResponseEntity<List<BookingDetailsResponse>> getUpcomingBookings(
            @Parameter(description = "Club ID", required = true)
            @PathVariable("clubId") Long clubId,

            @Parameter(description = "Time window in hours (e.g., 12)", example = "12")
            @RequestParam(name = "window", defaultValue = "12") Long windowDurationHr,

            @Parameter(description = "Optional list of station types to filter by")
            @RequestParam(name = "stationTypes", required = false) List<StationType> stationTypes
    ) {
        List<BookingDetailsResponse> bookings =
                bookingService.getUpcomingBookingsByClubId(clubId, windowDurationHr, stationTypes);
        return ResponseEntity.ok(bookings);
    }

}
