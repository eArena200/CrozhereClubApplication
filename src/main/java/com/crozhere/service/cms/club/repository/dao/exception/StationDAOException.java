package com.crozhere.service.cms.club.repository.dao.exception;

public class StationDAOException extends RuntimeException {
    public StationDAOException() {
        super();
    }

    public StationDAOException(String message) {
        super(message);
    }

    public StationDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationDAOException(Throwable cause) {
        super(cause);
    }
}
