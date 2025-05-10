package com.crozhere.service.cms.booking.controller.model.request;


import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingRequest {
    private String stationId;
    private String playerId;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer players;
}
