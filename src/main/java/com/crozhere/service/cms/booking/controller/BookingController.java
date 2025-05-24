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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody CreateBookingRequest createBookingRequest){
        Booking booking = bookingService.createBooking(createBookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(getBookingResponse(booking));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable("bookingId") Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(getBookingResponse(booking));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable("bookingId") Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(getBookingResponse(booking));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<BookingResponse>> listBookingsByPlayer(
            @PathVariable("playerId") Long playerId) {
        List<Booking> bookings = bookingService.listBookingByPlayerId(playerId);
        List<BookingResponse> responses = bookings.stream()
                .map(this::getBookingResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<BookingResponse>> listBookingsByClub(
            @PathVariable("clubId") Long clubId) {
        List<Booking> bookings = bookingService.listBookingByClubId(clubId);
        List<BookingResponse> responses = bookings.stream()
                .map(this::getBookingResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }


    @PostMapping("/availability/by-time")
    public ResponseEntity<BookingAvailabilityByTimeResponse> checkAvailabilityByTime(
            @Valid @RequestBody BookingAvailabilityByTimeRequest request){
        BookingAvailabilityByTimeResponse response =
                bookingService.checkAvailabilityByTime(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/availability/by-station")
    public ResponseEntity<BookingAvailabilityByStationResponse> checkAvailabilityByStation(
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
