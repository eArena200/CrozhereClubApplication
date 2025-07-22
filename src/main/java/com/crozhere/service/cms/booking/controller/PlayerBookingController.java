package com.crozhere.service.cms.booking.controller;

import com.crozhere.service.cms.booking.controller.model.request.CreatePlayerBookingIntentRequest;
import com.crozhere.service.cms.booking.controller.model.response.*;
import com.crozhere.service.cms.booking.service.BookingService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(
    value = "/booking/player",
    produces = "application/json"
)
@Tag(
    name = "Player Bookings",
    description = "APIs for players to manage their bookings and booking intents"
)
public class PlayerBookingController {

    private final BookingService bookingService;

    @Autowired
    public PlayerBookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "CreateBookingIntentForPlayer",
        description = "Creates a booking intent by a player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking intent created successfully",
            content = @Content(schema = @Schema(implementation = BookingIntentDetailsResponse.class))
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
    @PreAuthorize("hasRole('PLAYER')")
    @PostMapping(
        value = "/createBookingIntent",
        consumes = "application/json"
    )
    public ResponseEntity<BookingIntentDetailsResponse> createBookingIntentForPlayer(
        @Parameter(description = "Booking intent creation request body", required = true)
        @Valid @RequestBody CreatePlayerBookingIntentRequest request
    ) {
        Long playerId = AuthUtil.getRoleBasedId();
        BookingIntentDetailsResponse response =
                bookingService.createBookingIntentForPlayer(playerId, request);
        return ResponseEntity.ok(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "GetActiveBookingIntentsForPlayer",
        description = "Returns all active booking intents for the given player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Active intents fetched successfully",
            content = @Content(array = @ArraySchema(
                schema = @Schema(implementation = BookingIntentDetailsResponse.class)))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/getActiveIntents")
    public ResponseEntity<List<BookingIntentDetailsResponse>> getActiveIntentsForPlayer(){
        Long playerId = AuthUtil.getRoleBasedId();
        List<BookingIntentDetailsResponse> activeIntents =
                bookingService.getActiveIntentsForPlayer(playerId);
        return ResponseEntity.ok(activeIntents);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "CancelBookingIntentForPlayer",
        description = "Cancels a booking intent for the given player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking intent cancelled successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or already cancelled",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('PLAYER')")
    @PutMapping(
        value = "/cancelBookingIntent/{intentId}",
        consumes = "application/json"
    )
    public ResponseEntity<Void> cancelBookingIntentForPlayer(
        @Parameter(name = "intentId", description = "Booking Intent ID", required = true)
        @PathVariable(value = "intentId") Long intentId
    ) {
        Long playerId = AuthUtil.getRoleBasedId();
        bookingService.cancelPlayerBookingIntent(playerId, intentId);
        return ResponseEntity.ok().build();
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "GetBookingByIntentIdForPlayer",
        description = "Retrieves a booking based on the intent ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking found",
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
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/getBookingByIntentId/{intentId}")
    public ResponseEntity<BookingDetailsResponse> getBookingByIntentId(
        @Parameter(description = "Booking Intent ID", required = true)
        @PathVariable("intentId") Long intentId
    ) {
        Long playerId = AuthUtil.getRoleBasedId();
        BookingDetailsResponse response =
                bookingService.getPlayerBookingByIntentId(playerId, intentId);
        return ResponseEntity.ok(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "GetBookingDetailsForPlayer",
        description = "Retrieves booking details for the given player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking found",
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
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/getBookingDetails/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBookingDetails(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable("bookingId") Long bookingId
    ) {
        Long playerId = AuthUtil.getRoleBasedId();
        BookingDetailsResponse response =
                bookingService.getPlayerBookingById(playerId, bookingId);
        return ResponseEntity.ok(response);
    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "CancelBookingForPlayer",
        description = "Cancels a confirmed booking for a player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking cancelled successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or already cancelled",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
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
    @PreAuthorize("hasRole('PLAYER')")
    @PutMapping(
        value = "/cancelBooking/{bookingId}",
        consumes = "application/json"
    )
    public ResponseEntity<Void> cancelBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable("bookingId") Long bookingId
    ) {
        Long playerId = AuthUtil.getRoleBasedId();
        bookingService.cancelPlayerBooking(playerId, bookingId);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "ListBookingsForPlayer",
        description = "Fetches all confirmed bookings for the given player"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Bookings fetched successfully",
            content = @Content(array = @ArraySchema(
                schema = @Schema(implementation = BookingDetailsResponse.class)))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class))
        )
    })
    @PreAuthorize("hasRole('PLAYER')")
    @GetMapping("/getBookings")
    public ResponseEntity<List<BookingDetailsResponse>> listBookingsForPlayer() {
        Long playerId = AuthUtil.getRoleBasedId();
        List<BookingDetailsResponse> bookings =
                bookingService.listBookingByPlayerId(playerId);
        return ResponseEntity.ok(bookings);
    }
}
