package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
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
public class BookingAvailabilityByTimeRequest {

    @NotNull
    private Long clubId;

    @NotNull
    private StationType stationType;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;
}
