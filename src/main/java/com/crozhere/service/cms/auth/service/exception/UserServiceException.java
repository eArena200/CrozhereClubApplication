package com.crozhere.service.cms.auth.service.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {
    private final UserServiceExceptionType type;

    public UserServiceException(UserServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public UserServiceException(UserServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }
}
