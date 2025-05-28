package com.crozhere.service.cms.booking.controller.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchWindow {
    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private Integer windowHrs;
}
