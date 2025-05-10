package com.crozhere.service.cms.booking.repository.exception;

public class BookingDAOException extends Exception {
    public BookingDAOException() {
        super();
    }

    public BookingDAOException(String message) {
        super(message);
    }

    public BookingDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingDAOException(Throwable cause) {
        super(cause);
    }
}
