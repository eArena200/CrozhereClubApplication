package com.crozhere.service.cms.club.service.exception;

import lombok.Getter;

@Getter
public class RateCardServiceException extends RuntimeException {

    private final RateCardServiceExceptionType type;

    public RateCardServiceException(RateCardServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public RateCardServiceException(RateCardServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }
}
