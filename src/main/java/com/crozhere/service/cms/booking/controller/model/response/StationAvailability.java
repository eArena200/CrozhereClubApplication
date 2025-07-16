package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationAvailability {
    private Long stationId;
    private boolean isAvailable;
}

