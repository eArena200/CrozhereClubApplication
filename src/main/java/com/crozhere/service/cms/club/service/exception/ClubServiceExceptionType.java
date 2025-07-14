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
    TOGGLE_STATION_STATUS("Failed to toggle station status"),

    GET_STATIONS_BY_CLUB_FAILED("Failed to get stations by club."),
    GET_STATIONS_BY_CLUBS_FAILED("Failed get stations for clubIds."),
    GET_STATIONS_BY_TYPE_FAILED("Failed to get stations by type.");

    private final String message;

    ClubServiceExceptionType(String message) {
        this.message = message;
    }

}
