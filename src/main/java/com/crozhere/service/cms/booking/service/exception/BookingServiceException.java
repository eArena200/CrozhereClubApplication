package com.crozhere.service.cms.booking.service.exception;

import lombok.Getter;

@Getter
public class BookingServiceException extends RuntimeException {

    private final BookingServiceExceptionType type;

    public BookingServiceException(BookingServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public BookingServiceException(BookingServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

}
