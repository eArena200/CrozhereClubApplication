package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
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
public class BookingDetails {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalPlayers;

    private StationType stationType;
    private List<BookingStationDetails> stations;

    private BookingStatus bookingStatus;
    private BookingCostDetails costDetails;
}
