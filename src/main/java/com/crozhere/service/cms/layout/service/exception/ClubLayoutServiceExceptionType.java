package com.crozhere.service.cms.layout.service.exception;

import lombok.Getter;

@Getter
public enum ClubLayoutServiceExceptionType {

    CLUB_LAYOUT_ALREADY_EXISTS("A layout for this club already exists."),
    CLUB_LAYOUT_NOT_FOUND("Club layout not found."),
    CREATE_CLUB_LAYOUT_FAILED("Failed to create club layout."),
    GET_CLUB_LAYOUT_FAILED("Failed to retrieve club layout."),
    DELETE_CLUB_LAYOUT_FAILED("Failed to delete club layout."),

    ZONE_LAYOUT_NOT_FOUND("Zone layout not found."),
    ADD_ZONE_LAYOUT_FAILED("Failed to add zone layout."),
    UPDATE_ZONE_LAYOUT_FAILED("Failed to update zone layout."),
    GET_ZONE_LAYOUT_FAILED("Failed to retrieve zone layout."),
    DELETE_ZONE_LAYOUT_FAILED("Failed to delete zone layout."),

    GROUP_LAYOUT_NOT_FOUND("Station group layout not found."),
    ADD_GROUP_LAYOUT_FAILED("Failed to add station group layout."),
    GET_GROUP_LAYOUT_FAILED("Failed to retrieve station group layout."),
    UPDATE_GROUP_LAYOUT_FAILED("Failed to update station group layout."),
    DELETE_GROUP_LAYOUT_FAILED("Failed to delete station group layout."),

    STATION_LAYOUT_ALREADY_EXISTS("A station layout already exists for this station."),
    STATION_LAYOUT_NOT_FOUND("Station layout not found."),
    GET_STATION_LAYOUT_FAILED("Failed to retrieve station layout."),
    ADD_STATION_LAYOUT_FAILED("Failed to add station layout."),
    UPDATE_STATION_LAYOUT_FAILED("Failed to update station layout."),
    DELETE_STATION_LAYOUT_FAILED("Failed to delete station layout.");

    private final String message;

    ClubLayoutServiceExceptionType(String message) {
        this.message = message;
    }

}
