package com.crozhere.service.cms.user.service.exception;

import lombok.Getter;

@Getter
public enum ClubAdminServiceExceptionType {
    CLUB_ADMIN_NOT_FOUND("Club admin not found."),
    CREATE_CLUB_ADMIN_FAILED("Failed to create club admin."),
    GET_CLUB_ADMIN_FAILED("Failed to retrieve club admin."),
    UPDATE_CLUB_ADMIN_FAILED("Failed to update club admin."),
    DELETE_CLUB_ADMIN_FAILED("Failed to delete club admin.");

    private final String message;

    ClubAdminServiceExceptionType(String message) {
        this.message = message;
    }

}
