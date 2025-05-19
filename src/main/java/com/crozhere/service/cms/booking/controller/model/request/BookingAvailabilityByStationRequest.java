package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingAvailabilityByStationRequest {

    @NotNull
    private Long clubId;

    @NotBlank
    private StationType stationType;

    @NotEmpty
    private List<Long> stationIds;
}

