package com.crozhere.service.cms.club.service.exception;

public class ClubServiceException extends Exception {

    public ClubServiceException() {
        super();
    }

    public ClubServiceException(String message) {
        super(message);
    }

    public ClubServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClubServiceException(Throwable cause) {
        super(cause);
    }
}
