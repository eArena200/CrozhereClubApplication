package com.crozhere.service.cms.player.controller.advice;

import com.crozhere.service.cms.player.controller.model.response.ErrorResponse;
import com.crozhere.service.cms.player.service.exception.PlayerServiceException;
import com.crozhere.service.cms.player.service.exception.PlayerServiceExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.crozhere.service.cms.player")
public class PlayerServiceExceptionHandler {

    @ExceptionHandler(PlayerServiceException.class)
    public ResponseEntity<ErrorResponse> handlePlayerServiceException(
            PlayerServiceException ex) {

        PlayerServiceExceptionType type = ex.getType();
        HttpStatus status = resolveHttpStatus(type);

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

    private HttpStatus resolveHttpStatus(PlayerServiceExceptionType type) {
        String name = type.name();

        if (name.endsWith("_NOT_FOUND")) return HttpStatus.NOT_FOUND;
        if (name.endsWith("_FAILED")) return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.BAD_REQUEST;
    }
}
