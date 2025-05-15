package com.crozhere.service.cms.club.repository.dao.exception;

public class ClubDAOException extends RuntimeException {

    public ClubDAOException() {
        super();
    }

    public ClubDAOException(String message) {
        super(message);
    }

    public ClubDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClubDAOException(Throwable cause) {
        super(cause);
    }
}
