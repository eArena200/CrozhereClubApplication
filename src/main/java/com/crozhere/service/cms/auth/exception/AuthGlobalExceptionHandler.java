package com.crozhere.service.cms.auth.exception;

import com.crozhere.service.cms.auth.controller.model.response.ErrorResponse;
import com.crozhere.service.cms.auth.service.exception.AuthServiceException;
import com.crozhere.service.cms.auth.service.exception.AuthServiceExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.crozhere.service.cms.auth")
public class AuthGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse error = ErrorResponse.builder()
                .error("ValidationException")
                .type("INVALID_REQUEST")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthServiceException(
            AuthServiceException ex) {

        AuthServiceExceptionType type = ex.getType();
        HttpStatus status = resolveHttpStatus(type);

        log.error("Handled AuthServiceException [{}]: {}", type.name(),
                type.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("AuthServiceException")
                .type(type.name())
                .message(type.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        log.error("Unhandled exception", ex);

        ErrorResponse error = ErrorResponse.builder()
                .error("InternalServerError")
                .type("UNKNOWN_EXCEPTION")
                .message("Something went wrong")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    private HttpStatus resolveHttpStatus(AuthServiceExceptionType type) {
        String name = type.name();

        if (name.endsWith("_NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (name.startsWith("INVALID")) return HttpStatus.BAD_REQUEST;
        if (name.endsWith("_FAILED")) return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.BAD_REQUEST;
    }
}

