package com.crozhere.service.cms.booking.service.engine;

import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.club.controller.model.response.RateResponse;
import com.crozhere.service.cms.club.repository.entity.Rate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class BookingContext {
    private final BookingIntent bookingIntent;
    private final Map<Long, Long> stationToRateMap;
    private final Map<Long, RateResponse> rateMap;
}
