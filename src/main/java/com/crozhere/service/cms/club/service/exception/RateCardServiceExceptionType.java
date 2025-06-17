package com.crozhere.service.cms.club.service.exception;

import lombok.Getter;

@Getter
public enum RateCardServiceExceptionType {
    RATE_CARD_NOT_FOUND("Rate card not found"),
    CREATE_RATE_CARD_FAILED("Failed to create rate-card"),
    GET_RATE_CARD_FAILED("Failed to retrieve rate-card"),
    UPDATE_RATE_CARD_FAILED("Failed to update rate-card"),
    DELETE_RATE_CARD_FAILED("Failed to delete rate-card"),
    ADD_RATE_FAILED("Failed to add rate"),
    RATE_NOT_FOUND("Rate not found"),
    FETCH_RATES_FAILED("Failed to retrieve rates"),
    UPDATE_RATE_FAILED("Failed to update rate"),
    DELETE_RATE_FAILED("Failed to delete rate");

    private final String message;

    RateCardServiceExceptionType(String message) {
        this.message = message;
    }
}
