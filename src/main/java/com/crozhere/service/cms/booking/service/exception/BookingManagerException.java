package com.crozhere.service.cms.booking.service.exception;

public class BookingManagerException extends RuntimeException {

    public BookingManagerException() {
        super();
    }

    public BookingManagerException(String message) {
        super(message);
    }

    public BookingManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingManagerException(Throwable cause) {
        super(cause);
    }
}
