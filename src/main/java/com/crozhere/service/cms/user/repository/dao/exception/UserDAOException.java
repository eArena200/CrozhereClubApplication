package com.crozhere.service.cms.user.repository.dao.exception;

public class UserDAOException extends RuntimeException {
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
