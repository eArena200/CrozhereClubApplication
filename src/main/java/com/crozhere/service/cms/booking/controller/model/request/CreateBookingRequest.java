package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private Long playerId;
    private Long clubId;
    private List<Long> stationIds;
    private StationType stationType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer players;
}
