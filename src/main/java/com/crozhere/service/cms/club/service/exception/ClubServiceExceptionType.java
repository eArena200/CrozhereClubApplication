package com.crozhere.service.cms.club.service.exception;


import lombok.Getter;

@Getter
public enum ClubServiceExceptionType {
    CLUB_NOT_FOUND("Club not found."),
    CREATE_CLUB_FAILED("Failed to create club."),
    DELETE_CLUB_FAILED("Failed to delete club."),
    GET_CLUB_FAILED("Failed to retrieve club."),
    GET_CLUBS_FAILED("Failed to retrieve clubs."),
    UPDATE_CLUB_FAILED("Failed to update club."),

    STATION_NOT_FOUND("Station not found."),
    ADD_STATION_FAILED("Failed to add station."),
    DELETE_STATION_FAILED("Failed to delete station."),
    GET_STATION_FAILED("Failed to get station."),
    UPDATE_STATION_FAILED("Failed to update station."),
    TOGGLE_STATION_STATUS_FAILED("Failed to toggle station status"),

    GET_STATIONS_BY_CLUB_FAILED("Failed to get stations by club."),
    GET_STATIONS_BY_CLUBS_FAILED("Failed get stations for clubIds."),
    GET_STATIONS_BY_TYPE_FAILED("Failed to get stations by type."),

    RATE_CARD_NOT_FOUND("Rate card not found"),
    CREATE_RATE_CARD_FAILED("Failed to create rate-card"),
    GET_RATE_CARD_FAILED("Failed to retrieve rate-card"),
    UPDATE_RATE_CARD_FAILED("Failed to update rate-card"),
    DELETE_RATE_CARD_FAILED("Failed to delete rate-card"),
    ADD_RATE_FAILED("Failed to add rate"),
    RATE_NOT_FOUND("Rate not found"),
    GET_RATE_FAILED("Get Rate Failed"),
    FETCH_RATES_FAILED("Failed to retrieve rates"),
    UPDATE_RATE_FAILED("Failed to update rate"),
    DELETE_RATE_FAILED("Failed to delete rate");

    private final String message;

    ClubServiceExceptionType(String message) {
        this.message = message;
    }

}
