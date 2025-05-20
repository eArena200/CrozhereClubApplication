package com.crozhere.service.cms.booking.controller.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SearchWindow {
    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private Integer windowHrs;
}
