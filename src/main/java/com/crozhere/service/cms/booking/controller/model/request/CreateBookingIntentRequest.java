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
    // should be present for playerSide booking
    private Long playerId;

    // should be present for adminSide booking
    private String playerPhoneNumber;

    private Long clubId;
    private List<Long> stationIds;
    private StationType stationType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer players;
}
