package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAvailabilityByStationRequest {

    @NotNull
    private Long clubId;

    @NotNull
    private StationType stationType;

    @NotEmpty
    private List<BookingStationRequest> stations;

    @NotNull
    private Integer durationHrs;

    @NotNull
    private SearchWindow searchWindow;
}

