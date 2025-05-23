package com.crozhere.service.cms.layout.service.exception;

public class ClubLayoutServiceException extends RuntimeException {

    public ClubLayoutServiceException(String message) {
        super(message);
    }

    public ClubLayoutServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClubLayoutServiceException(Throwable cause) {
        super(cause);
    }
}
