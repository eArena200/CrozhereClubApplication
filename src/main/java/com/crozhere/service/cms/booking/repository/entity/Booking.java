package com.crozhere.service.cms.booking.repository.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    private String bookingId;
    private String playerId;
    private String stationId;
    private String paymentId;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer playersCount;
}
