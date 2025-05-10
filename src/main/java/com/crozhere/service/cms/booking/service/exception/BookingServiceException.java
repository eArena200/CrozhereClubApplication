package com.crozhere.service.cms.booking.service.exception;

public class BookingServiceException extends Exception{
    public BookingServiceException() {
        super();
    }

    public BookingServiceException(String message) {
        super(message);
    }

    public BookingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingServiceException(Throwable cause) {
        super(cause);
    }
}
