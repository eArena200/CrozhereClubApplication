package com.crozhere.service.cms.club.service.exception;

public class ClubAdminServiceException extends RuntimeException {

    public ClubAdminServiceException() {
        super();
    }

    public ClubAdminServiceException(String message) {
        super(message);
    }

    public ClubAdminServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClubAdminServiceException(Throwable cause) {
        super(cause);
    }
}
