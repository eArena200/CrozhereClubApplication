package com.crozhere.service.cms.player.service.exception;

public class PlayerServiceException extends Exception {

    public PlayerServiceException() {
        super();
    }

    public PlayerServiceException(String message) {
        super(message);
    }

    public PlayerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerServiceException(Throwable cause) {
        super(cause);
    }
}
