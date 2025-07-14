package com.crozhere.service.cms.booking.controller.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStationRequest {
    @NotNull
    private Long stationId;

    @NotNull
    private Integer playerCount;
}
