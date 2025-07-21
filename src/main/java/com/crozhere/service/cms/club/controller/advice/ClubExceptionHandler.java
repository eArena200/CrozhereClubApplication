package com.crozhere.service.cms.club.controller.advice;

import com.crozhere.service.cms.club.controller.model.response.ServiceErrorResponse;
import com.crozhere.service.cms.club.service.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.crozhere.service.cms.club")
public class ClubExceptionHandler {

    @ExceptionHandler(ClubServiceException.class)
    public ResponseEntity<ServiceErrorResponse> handleClubServiceException(
            ClubServiceException ex) {

        ClubServiceExceptionType type = ex.getType();
        HttpStatus status = resolveHttpStatus(type.name());

        log.error("Handled ClubServiceException [{}]: {}", type.name(), type.getMessage(), ex);

        ServiceErrorResponse error = ServiceErrorResponse.builder()
                .error("ClubServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(RateCardServiceException.class)
    public ResponseEntity<ServiceErrorResponse> handleRateCardServiceException(
            RateCardServiceException ex) {

        RateCardServiceExceptionType type = ex.getType();
        HttpStatus status = resolveHttpStatus(type.name());

        log.error("Handled RateCardServiceException [{}]: {}", type.name(), type.getMessage(), ex);

        ServiceErrorResponse error = ServiceErrorResponse.builder()
                .error("RateCardServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    private HttpStatus resolveHttpStatus(String name) {
        if (name.endsWith("_NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (name.endsWith("_FAILED")) return HttpStatus.INTERNAL_SERVER_ERROR;
        return HttpStatus.BAD_REQUEST;
    }
}
