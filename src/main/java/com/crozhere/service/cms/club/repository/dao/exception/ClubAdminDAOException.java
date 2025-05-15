package com.crozhere.service.cms.club.repository.dao.exception;

public class ClubAdminDAOException extends RuntimeException {

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
