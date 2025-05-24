package com.crozhere.service.cms.player.service.exception;

import lombok.Getter;

@Getter
public enum PlayerServiceExceptionType {

    PLAYER_NOT_FOUND("Player not found."),
    CREATE_PLAYER_FAILED("Failed to create player."),
    GET_PLAYER_FAILED("Failed to retrieve player."),
    UPDATE_PLAYER_FAILED("Failed to update player."),
    DELETE_PLAYER_FAILED("Failed to delete player.");

    private final String message;

    PlayerServiceExceptionType(String message) {
        this.message = message;
    }

}
