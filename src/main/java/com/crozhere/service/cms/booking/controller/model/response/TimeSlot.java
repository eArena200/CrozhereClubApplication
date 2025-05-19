package com.crozhere.service.cms.booking.controller.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

