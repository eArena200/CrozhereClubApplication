package com.crozhere.service.cms.auth.repository.exception;

public class UserDAOException extends Exception {
    public UserDAOException() {
        super();
    }

    public UserDAOException(String message) {
        super(message);
    }

    public UserDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDAOException(Throwable cause) {
        super(cause);
    }
}
