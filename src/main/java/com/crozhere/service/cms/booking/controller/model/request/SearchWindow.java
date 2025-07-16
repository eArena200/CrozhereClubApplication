package com.crozhere.service.cms.booking.controller.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchWindow {
    @NotNull
    private Instant dateTime;

    @NotNull
    @Min(1)
    @Max(24)
    private Integer windowHrs;
}
