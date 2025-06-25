package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingIntentRequest {
    // optional for adminSide booking
    private Long playerId;

    private String playerPhoneNumber;
    private Long clubId;
    private List<Long> stationIds;
    private StationType stationType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer players;
}
