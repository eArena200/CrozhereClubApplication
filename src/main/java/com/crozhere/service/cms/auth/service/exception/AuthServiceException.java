package com.crozhere.service.cms.auth.service.exception;

import lombok.Getter;

@Getter
public class AuthServiceException extends RuntimeException {
    private final AuthServiceExceptionType type;

    public AuthServiceException(AuthServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public AuthServiceException(AuthServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

}

