package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingAvailabilityByStationResponse {
    private Long clubId;
    private StationType stationType;
    private List<Long> stationIds;
    private List<LocalDateTime> availableTimes;
}
