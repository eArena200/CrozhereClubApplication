package com.crozhere.service.cms.booking.controller.model.response;

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
public class BookingIntentResponse {
    private Long intentId;
    private Long clubId;

    private Long playerId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expiresAt;

    private StationType stationType;
    private List<Long> stationIds;
    private Integer players;

    private Boolean isConfirmed;
    private Double totalCost;
}
