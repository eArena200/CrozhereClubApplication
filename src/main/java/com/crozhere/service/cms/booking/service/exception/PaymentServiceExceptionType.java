package com.crozhere.service.cms.booking.service.exception;

import lombok.Getter;

@Getter
public enum PaymentServiceExceptionType {
    PAYMENT_CREATION_FAILED("Failed to create payment"),
    PAYMENT_CONFIRMATION_FAILED("Failed to confirm payment"),
    INVALID_PAYMENT_DETAILS("Invalid payment details"),
    INVALID_REQUEST("Invalid Request"),
    PAYMENT_NOT_FOUND("Payment not found"),
    GET_PAYMENT_FAILED("Failed to retrieve payment"),
    UNSUPPORTED_PAYMENT_MODE("Payment mode not supported"),
    PAYMENT_PROVIDER_ERROR("Payment provider error");

    private final String message;

    PaymentServiceExceptionType(String message) {
        this.message = message;
    }
}
