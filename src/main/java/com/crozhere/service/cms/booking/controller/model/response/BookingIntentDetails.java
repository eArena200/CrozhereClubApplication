package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingIntentDetails {
    private Instant startTime;
    private Instant endTime;
    private Instant expiresAt;

    private StationType stationType;
    private List<BookingIntentStationDetails> stations;
    private Integer totalPlayerCount;

    private Boolean isCancelled;
    private Boolean isConfirmed;

    private BookingIntentCostDetails costDetails;
}
