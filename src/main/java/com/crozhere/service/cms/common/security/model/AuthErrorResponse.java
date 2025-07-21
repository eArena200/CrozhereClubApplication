package com.crozhere.service.cms.common.security.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
public class AuthErrorResponse {
    private int status;
    private String message;
    private String path;
    private Date timestamp;
}