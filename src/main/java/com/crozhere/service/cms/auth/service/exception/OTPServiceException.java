package com.crozhere.service.cms.auth.service.exception;

public class OTPServiceException extends Exception {
    public OTPServiceException() {
        super();
    }

    public OTPServiceException(String message) {
        super(message);
    }

    public OTPServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OTPServiceException(Throwable cause) {
        super(cause);
    }
}
