package com.crozhere.service.cms.booking.controller.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class BookingResponse {
    private String bookingId;
    private String stationId;
    private String playerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer players;
}
