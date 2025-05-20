package com.crozhere.service.cms.booking.util;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

