package com.crozhere.service.cms.user.repository.exception;

public class ClubAdminDAOException extends Exception {
    public ClubAdminDAOException() {
        super();
    }

    public ClubAdminDAOException(String message) {
        super(message);
    }

    public ClubAdminDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClubAdminDAOException(Throwable cause) {
        super(cause);
    }
}
