package com.crozhere.service.cms.user.service.exception;

import lombok.Getter;

@Getter
public class PlayerServiceException extends RuntimeException {
    private final PlayerServiceExceptionType type;

    public PlayerServiceException(PlayerServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public PlayerServiceException(PlayerServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

}
