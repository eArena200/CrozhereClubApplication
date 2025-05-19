package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookingAvailabilityByTimeResponse {
    private Long clubId;
    private StationType stationType;
    private List<StationAvailability> stationsAvailability;
}
