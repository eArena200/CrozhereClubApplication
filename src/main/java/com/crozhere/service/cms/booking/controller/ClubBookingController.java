package com.crozhere.service.cms.booking.controller;

import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.controller.model.response.*;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.club.controller.model.response.RateResponse;
import com.crozhere.service.cms.club.repository.entity.StationType;
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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(
        value = "/booking/club",
        produces = "application/json"
)
@Tag(
        name = "Club Booking APIs",
        description = "APIs for clubs to manage their bookings and booking intents"
)
public class ClubBookingController {

    private final BookingService bookingService;

    @Autowired
    public ClubBookingController(
        BookingService bookingService
    ){
        this.bookingService = bookingService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "CreateBookingIntentForClub",
        description = "Creates a booking intent by a club admin for their club"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking intent created successfully",
            content = @Content(schema = @Schema(implementation = BookingIntentDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club not found",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
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
        value = "/createBookingIntent",
        consumes = "application/json"
    )
    public ResponseEntity<BookingIntentDetailsResponse> createBookingIntent(
        @Parameter(
            description = "Booking intent creation request body",
            required = true
        )
        @Valid
        @RequestBody
        CreateClubBookingIntentRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        BookingIntentDetailsResponse response =
                bookingService.createBookingIntentForClub(clubAdminId, request);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "ApplyClubDiscount",
            description = "Applies a manual booking discount from a club to a booking-intent"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Discount applied successfully",
                    content = @Content(schema = @Schema(implementation = BookingIntentDetailsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "BookingIntent not found",
                    content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
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
            value = "/applyClubDiscount/{bookingIntentId}",
            consumes = "application/json"
    )
    public ResponseEntity<BookingIntentDetailsResponse> applyClubDiscount(
            @Parameter(
                    name = "bookingIntentId",
                    description = "BookingIntentId to which discount needs to be applied",
                    required = true
            )
            @PathVariable(value = "bookingIntentId") Long bookingIntentId,
            @Parameter(
                    description = "DiscountRequestBody request body",
                    required = true
            )
            @Valid
            @RequestBody
            ClubDiscountRequest request
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        BookingIntentDetailsResponse response =
                bookingService.applyClubDiscount(clubAdminId, bookingIntentId, request);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "List active booking intents for a club",
        description = "Returns all currently active (not expired, not confirmed, not cancelled) booking intents for a club"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Active booking intents fetched successfully",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = BookingIntentDetailsResponse.class)))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Club Not found",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/getActiveIntents/{clubId}")
    public ResponseEntity<List<BookingIntentDetailsResponse>> getActiveIntents(
        @Parameter(
            name = "clubId",
            description = "Id for club for which active intents needs to be fetched",
            required = true
        )
        @PathVariable(value = "clubId") Long clubId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        List<BookingIntentDetailsResponse> activeIntents =
                bookingService.getActiveIntentsForClub(clubAdminId, clubId);
        return ResponseEntity.ok(activeIntents);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Cancel a booking intent",
        description = "Cancels a booking intent before it is confirmed or expires"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking intent cancelled successfully",
            content = @Content(schema = @Schema(implementation = BookingIntentDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking intent not found",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or intent already cancelled",
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
            value = "/cancelBookingIntent/{clubId}/{intentId}",
            consumes = "application/json"
    )
    public ResponseEntity<BookingIntentDetailsResponse> cancelBookingIntent(
        @Parameter(
            name = "clubId",
            description = "Club ID",
            required = true
        )
        @PathVariable(value = "clubId") Long clubId,

        @Parameter(
            name = "intentId",
            description = "Intent ID",
            required = true
        )
        @PathVariable(value = "intentId") Long intentId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        bookingService.cancelClubBookingIntent(clubAdminId, clubId, intentId);
        return ResponseEntity.ok().build();
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "ListBookingsForClub",
        description = "Retrieves all bookings for a given club with optional filters and pagination."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bookings fetched successfully",
            content = @Content(schema = @Schema(implementation = PaginatedListBookingsResponse.class))
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
    @PostMapping(
        value = "/getBookings/{clubId}",
        consumes = "application/json"
    )
    public ResponseEntity<PaginatedListBookingsResponse> listBookingsForClub(
        @Parameter(
            name = "clubId",
            description = "ID of the club",
            required = true
        )
        @PathVariable(value = "clubId") Long clubId,

        @Parameter(
            description = "Request body containing filters for bookings (all fields optional).",
            required = false
        )
        @Valid
        @RequestBody(required = false)
        ClubBookingsListFilterRequest filterRequest,

        @Parameter(
            name = "page",
            description = "Page number to fetch (1‑based). Defaults to 1.",
            example = "1"
        )
        @RequestParam(defaultValue = "1")
        Integer page,

        @Parameter(
            name = "pageSize",
            description = "Number of bookings per page. Maximum allowed is 100. Defaults to 10.",
            example = "10"
        )
        @RequestParam(defaultValue = "10")
        Integer pageSize
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        final int MAX_PAGE_SIZE = 100;
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;
        if (page < 1) page = 1;

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (filterRequest == null) filterRequest = new ClubBookingsListFilterRequest();

        Page<BookingDetailsResponse> bookingPage =
                bookingService.listBookingByClubIdWithFilters(
                        clubAdminId, clubId, filterRequest, pageable);

        PaginatedListBookingsResponse paginatedResponse = PaginatedListBookingsResponse.builder()
                .bookings(bookingPage.getContent())
                .totalCount(bookingPage.getTotalElements())
                .build();

        return ResponseEntity.ok(paginatedResponse);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "GetBookingByBookingIntentId",
        description = "Fetches booking details by booking intent ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking fetched successfully",
            content = @Content(schema = @Schema(implementation = BookingDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping(value = "/getBookingByIntentId/{clubId}/{intentId}")
    public ResponseEntity<BookingDetailsResponse> getBookingByIntentId(
        @Parameter(name = "clubId", description = "Club ID", required = true)
        @PathVariable(value = "clubId") Long clubId,

        @Parameter(name = "intentId", description = "Intent ID", required = true)
        @PathVariable(value = "intentId") Long intentId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        BookingDetailsResponse response =
                bookingService.getClubBookingByIntentId(
                        clubAdminId, clubId, intentId);
        return ResponseEntity.ok(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "GetBookingDetails",
        description = "Retrieve a booking for a club by booking ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking fetched successfully",
            content = @Content(schema = @Schema(implementation = BookingDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping(value = "/getBookingDetails/{clubId}/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBookingsForClub(
        @Parameter(name = "clubId", description = "Club ID", required = true)
        @PathVariable(value = "clubId") Long clubId,

        @Parameter(name = "bookingId", description = "Intent ID", required = true)
        @PathVariable(value = "bookingId") Long bookingId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        BookingDetailsResponse response =
                bookingService.getClubBookingById(
                        clubAdminId, clubId, bookingId);
        return ResponseEntity.ok(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cancel a booking for a club",
            description = "Cancel a booking by its ID for a specific club"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking cancelled successfully",
            content = @Content(schema = @Schema(implementation = BookingDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or booking cannot be cancelled",
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
        value = "/cancelBooking/{clubId}/{bookingId}",
        consumes = "application/json"
    )
    public ResponseEntity<BookingDetailsResponse> cancelBookingForClub(
        @Parameter(name = "clubId", description = "Club ID", required = true)
        @PathVariable(value = "clubId") Long clubId,

        @Parameter(name = "bookingId", description = "Intent ID", required = true)
        @PathVariable(value = "bookingId") Long bookingId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        bookingService.cancelClubBooking(clubAdminId, clubId, bookingId);
        return ResponseEntity.ok().build();
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Get dashboard station statuses for a club",
        description = "Returns current and upcoming booking info for each station in a club"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Station statuses fetched successfully"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping(value = "/dashboardStatus/{clubId}")
    public ResponseEntity<Map<Long, DashBoardStationStatusResponse>> getDashboardStationStatus(
        @Parameter(name = "clubId", description = "Club ID", required = true)
        @PathVariable(value = "clubId") Long clubId
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        Map<Long, DashBoardStationStatusResponse> response =
                bookingService.getDashboardStationStatusDetailsForClub(clubAdminId, clubId);
        return ResponseEntity.ok(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "List upcoming bookings for a club",
        description = "Returns upcoming confirmed bookings for the club within a given time window (default 12 hours)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Upcoming bookings fetched successfully",
                content = @Content(array = @ArraySchema(
                        schema = @Schema(implementation = BookingDetailsResponse.class)))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping(value = "/upcomingBookings/{clubId}")
    public ResponseEntity<List<BookingDetailsResponse>> getUpcomingBookings(
        @Parameter(name = "clubId", description = "Club ID", required = true)
        @PathVariable(value = "clubId") Long clubId,

        @Parameter(description = "Time window in hours (e.g., 12)", example = "12")
        @RequestParam(name = "window", defaultValue = "12") Long windowDurationHr,

        @Parameter(description = "Optional list of station types to filter by")
        @RequestParam(name = "stationTypes", required = false) List<StationType> stationTypes
    ) {
        Long clubAdminId = AuthUtil.getRoleBasedId();
        List<BookingDetailsResponse> bookings =
                bookingService.getUpcomingBookingsByClubId(
                        clubAdminId, clubId, windowDurationHr, stationTypes);
        return ResponseEntity.ok(bookings);
    }

}
