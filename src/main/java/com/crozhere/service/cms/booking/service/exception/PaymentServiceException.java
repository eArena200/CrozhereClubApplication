package com.crozhere.service.cms.booking.service.exception;

import lombok.Getter;

@Getter
public class PaymentServiceException extends RuntimeException {

    private final PaymentServiceExceptionType type;

    public PaymentServiceException(PaymentServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public PaymentServiceException(PaymentServiceExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    public PaymentServiceException(PaymentServiceExceptionType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public PaymentServiceException(PaymentServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }
}
