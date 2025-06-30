package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class BookingResponse {
    private Long bookingId;
    private Long clubId;
    private Long playerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer players;
    private StationType stationType;
    private List<Long> stationIds;
}
