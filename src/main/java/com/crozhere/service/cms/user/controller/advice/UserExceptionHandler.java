package com.crozhere.service.cms.user.controller.advice;

import com.crozhere.service.cms.user.service.exception.ClubAdminServiceException;
import com.crozhere.service.cms.user.service.exception.ClubAdminServiceExceptionType;
import com.crozhere.service.cms.user.controller.model.response.ErrorResponse;
import com.crozhere.service.cms.user.service.exception.PlayerServiceException;
import com.crozhere.service.cms.user.service.exception.PlayerServiceExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.crozhere.service.cms.user")
public class UserExceptionHandler {

    @ExceptionHandler(PlayerServiceException.class)
    public ResponseEntity<ErrorResponse> handlePlayerServiceException(
            PlayerServiceException ex) {

        PlayerServiceExceptionType type = ex.getType();
        HttpStatus status = resolvePlayerHttpStatus(type);

        log.error("Handled PlayerServiceException [{}]: {}", type.name(),
                type.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("PlayerServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ClubAdminServiceException.class)
    public ResponseEntity<ErrorResponse> handleClubAdminServiceException(
            ClubAdminServiceException ex) {

        ClubAdminServiceExceptionType type = ex.getType();
        HttpStatus status = resolveClubAdminHttpStatus(type);

        log.error("Handled ClubAdminServiceException [{}]: {}", type.name(), type.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("ClubAdminServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    private HttpStatus resolvePlayerHttpStatus(PlayerServiceExceptionType type) {
        String name = type.name();

        if (name.endsWith("_NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (name.endsWith("_FAILED")) return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.BAD_REQUEST;
    }

    private HttpStatus resolveClubAdminHttpStatus(ClubAdminServiceExceptionType type) {
        String name = type.name();

        if (name.endsWith("_NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (name.endsWith("_FAILED")) return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.BAD_REQUEST;
    }
}
