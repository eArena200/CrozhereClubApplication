package com.crozhere.service.cms.booking.controller;


import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingResponse;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(
            BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody CreateBookingRequest createBookingRequest){
        try {
            Booking booking = bookingService.createBooking(createBookingRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(getBookingResponse(booking));
        } catch (BookingServiceException bookingServiceException){
            log.error("Failed to create booking: {}", bookingServiceException.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable("bookingId") String bookingId) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(getBookingResponse(booking));
        } catch (BookingServiceException e) {
            log.error("Failed to retrieve booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable("bookingId") String bookingId) {
        try {
            Booking booking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(getBookingResponse(booking));
        } catch (BookingServiceException e) {
            log.error("Failed to cancel booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    private BookingResponse getBookingResponse(Booking booking){
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .stationId(booking.getStationId())
                .playerId(booking.getPlayerId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .players(booking.getPlayersCount())
                .build();
    }
}
