package com.crozhere.service.cms.booking.service.exception;

import lombok.Getter;

@Getter
public enum BookingServiceExceptionType {

    INVALID_AVAILABILITY("Invalid availability request."),
    BOOKING_NOT_FOUND("Booking not found."),
    GET_BOOKING_FAILED("Failed to retrieve booking."),
    CANCEL_BOOKING_FAILED("Failed to cancel booking."),
    LIST_BOOKINGS_BY_PLAYER_FAILED("Failed to list bookings by player."),
    LIST_BOOKINGS_BY_CLUB_FAILED("Failed to list bookings by club."),
    CHECK_AVAILABILITY_BY_TIME_FAILED("Failed to check availability by time."),
    CHECK_AVAILABILITY_BY_STATIONS_FAILED("Failed to check availability by stations."),

    CREATE_BOOKING_INTENT_FAILED("Failed to Create Booking intent"),
    BOOKING_INTENT_VALIDATION_FAILED("Failed to validate stations for booking-intent"),
    BOOKING_INTENT_NOT_FOUND("Booking intent not found"),
    GET_BOOKING_INTENT_FAILED("Failed to retrieve booking-intent"),
    CONFIRM_BOOKING_INTENT_FAILED("Failed to confirm booking");

    private final String message;

    BookingServiceExceptionType(String message) {
        this.message = message;
    }

}
