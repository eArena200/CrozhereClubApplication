package com.crozhere.service.cms.layout.service.exception;

import lombok.Getter;

@Getter
public class ClubLayoutServiceException extends RuntimeException {

    private final ClubLayoutServiceExceptionType type;

    public ClubLayoutServiceException(ClubLayoutServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public ClubLayoutServiceException(ClubLayoutServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

}
