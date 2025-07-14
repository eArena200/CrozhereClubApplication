package com.crozhere.service.cms.booking.controller.advice;

import com.crozhere.service.cms.booking.controller.model.response.ErrorResponse;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.BookingServiceExceptionType;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.crozhere.service.cms.booking")
public class BookingServiceExceptionHandler {

    @ExceptionHandler(BookingServiceException.class)
    public ResponseEntity<ErrorResponse> handleBookingServiceException(
            BookingServiceException ex) {

        BookingServiceExceptionType type = ex.getType();
        HttpStatus status = resolveHttpStatus(type);

        log.error("Handled BookingServiceException [{}]: {}", type.name(),
                type.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("BookingServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            InvalidRequestException ex) {

        log.error("Handled MethodArgumentNotValidException: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("InvalidRequestException")
                .type("INVALID_REQUEST")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(
            InvalidRequestException ex) {

        log.error("Handled InvalidRequestException: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("InvalidRequestException")
                .type("INVALID_REQUEST")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    private HttpStatus resolveHttpStatus(BookingServiceExceptionType type) {
        String name = type.name();

        if (name.endsWith("_NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (name.endsWith("_FAILED")) return HttpStatus.INTERNAL_SERVER_ERROR;
        if (name.startsWith("INVALID")) return HttpStatus.BAD_REQUEST;

        return HttpStatus.BAD_REQUEST;
    }
}
