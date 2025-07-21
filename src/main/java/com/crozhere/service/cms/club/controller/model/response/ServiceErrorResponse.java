package com.crozhere.service.cms.club.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceErrorResponse {
    private String error;
    private String type;
    private String message;
    private LocalDateTime timestamp;
}
