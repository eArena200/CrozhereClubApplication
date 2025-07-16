package com.crozhere.service.cms.booking.util;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TimeSlot {
    private Instant startTime;
    private Instant endTime;
}

