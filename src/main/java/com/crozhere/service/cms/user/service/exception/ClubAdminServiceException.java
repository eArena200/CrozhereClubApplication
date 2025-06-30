package com.crozhere.service.cms.user.service.exception;

import lombok.Getter;

@Getter
public class ClubAdminServiceException extends RuntimeException {

    private final ClubAdminServiceExceptionType type;

    public ClubAdminServiceException(ClubAdminServiceExceptionType type) {
        super(type.name());
        this.type = type;
    }

    public ClubAdminServiceException(ClubAdminServiceExceptionType type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

}
