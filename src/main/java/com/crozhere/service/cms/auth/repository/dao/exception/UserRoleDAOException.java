package com.crozhere.service.cms.auth.repository.dao.exception;

public class UserRoleDAOException extends Exception {

    public UserRoleDAOException() {
        super();
    }

    public UserRoleDAOException(String message) {
        super(message);
    }

    public UserRoleDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRoleDAOException(Throwable cause) {
        super(cause);
    }
}
