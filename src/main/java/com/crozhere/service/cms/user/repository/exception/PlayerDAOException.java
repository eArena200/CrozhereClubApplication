package com.crozhere.service.cms.user.repository.exception;

public class PlayerDAOException extends Exception {
    public PlayerDAOException() {
        super();
    }

    public PlayerDAOException(String message) {
        super(message);
    }

    public PlayerDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerDAOException(Throwable cause) {
        super(cause);
    }
}
