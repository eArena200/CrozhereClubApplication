package com.crozhere.service.cms.user.repository.dao.exception;

public class UserRoleDAOException extends RuntimeException {

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
