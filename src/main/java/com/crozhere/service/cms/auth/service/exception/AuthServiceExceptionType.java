package com.crozhere.service.cms.auth.service.exception;

import lombok.Getter;

@Getter
public enum AuthServiceExceptionType {

    INVALID_OTP("Invalid OTP provided."),
    INIT_AUTH_FAILED("Failed to initiate authentication."),
    VERIFY_AUTH_FAILED("Failed to verify authentication."),
    CREATE_PLAYER_FAILED("Failed to create player profile."),
    CREATE_CLUB_ADMIN_FAILED("Failed to create club admin profile."),
    GENERATE_TOKEN_FAILED("Failed to generate JWT token."),
    GET_PLAYER_PROFILE_FAILED("Failed to retrieve player profile."),
    GET_CLUB_ADMIN_PROFILE_FAILED("Failed to retrieve club admin profile.");

    private final String message;

    AuthServiceExceptionType(String message) {
        this.message = message;
    }
}
