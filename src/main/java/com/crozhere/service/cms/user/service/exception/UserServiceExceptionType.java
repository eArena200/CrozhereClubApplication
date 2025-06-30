package com.crozhere.service.cms.user.service.exception;

import lombok.Getter;

@Getter
public enum UserServiceExceptionType {
    USER_NOT_FOUND("User not found"),
    CREATE_USER_FAILED("Failed to create new user"),
    GET_USER_FAILED("Failed to get user"),
    UPDATE_USER_FAILED("Failed to update user"),
    DELETE_USER_FAILED("Failed to delete user"),
    ASSIGN_ROLE_FAILED("Failed to assign role"),
    REMOVE_ROLE_FAILED("Failed to remove role"),
    GET_USER_ROLES_FAILED("Failed to get roles for user"),
    CHECK_ROLE_FAILED("Failed to check role");

    private final String message;

    UserServiceExceptionType(String message){
        this.message = message;
    }
}
