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
    LIST_UPCOMING_BOOKINGS_BY_CLUB_FAILED("Failed to list upcoming bookings by club."),
    GET_DASHBOARD_STATION_STATUS_BY_CLUB_FAILED("Failed to get Dashboard station status by club"),
    CHECK_AVAILABILITY_BY_TIME_FAILED("Failed to check availability by time."),
    CHECK_AVAILABILITY_BY_STATIONS_FAILED("Failed to check availability by stations."),

    CREATE_BOOKING_INTENT_FAILED("Failed to Create Booking intent"),
    BOOKING_INTENT_VALIDATION_FAILED("Failed to validate stations for booking-intent"),
    BOOKING_INTENT_NOT_FOUND("Booking intent not found"),
    BOOKING_INTENT_EXPIRED("Booking-intent expired"),
    BOOKING_INTENT_NOT_CANCELLABLE("Booking-intent is not cancellable"),
    BOOKING_INTENT_ALREADY_USED("Booking intent already used"),
    GET_BOOKING_INTENT_FAILED("Failed to retrieve booking-intent"),
    GET_ACTIVE_INTENTS_FAILED("Failed to retrieve active intents"),
    CONFIRM_BOOKING_INTENT_FAILED("Failed to confirm booking"),
    CANCEL_BOOKING_INTENT_FAILED("Failed to cancel booking-intent");

    private final String message;

    BookingServiceExceptionType(String message) {
        this.message = message;
    }

}
