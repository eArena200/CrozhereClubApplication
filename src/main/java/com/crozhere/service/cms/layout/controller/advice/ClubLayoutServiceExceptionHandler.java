package com.crozhere.service.cms.layout.controller.advice;

import com.crozhere.service.cms.layout.controller.model.response.ErrorResponse;
import com.crozhere.service.cms.layout.service.exception.ClubLayoutServiceException;
import com.crozhere.service.cms.layout.service.exception.ClubLayoutServiceExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.crozhere.service.cms.layout")
public class ClubLayoutServiceExceptionHandler {

    @ExceptionHandler(ClubLayoutServiceException.class)
    public ResponseEntity<ErrorResponse> handleClubLayoutServiceException(
            ClubLayoutServiceException ex) {

        ClubLayoutServiceExceptionType type = ex.getType();
        HttpStatus status = resolveHttpStatus(type);

        log.error("Handled ClubLayoutServiceException [{}]: {}", type.name(), type.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("ClubLayoutServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .error("Exception")
                        .type("UNKNOWN")
                        .message("Unknown error occurred")
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    private HttpStatus resolveHttpStatus(ClubLayoutServiceExceptionType type) {
        String name = type.name();

        if (name.endsWith("_ALREADY_EXISTS")) {
            return HttpStatus.BAD_REQUEST;
        }

        if (name.endsWith("_NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
