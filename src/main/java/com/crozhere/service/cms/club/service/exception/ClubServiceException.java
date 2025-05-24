package com.crozhere.service.cms.club.service.exception;

import lombok.Getter;

@Getter
public class ClubServiceException extends RuntimeException {
    private final ClubServiceExceptionType type;

    public ClubServiceException(ClubServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public ClubServiceException(ClubServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

}
