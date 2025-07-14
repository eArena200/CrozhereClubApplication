package com.crozhere.service.cms.booking.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStation {
    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "player_count", nullable = false)
    private Integer playerCount;
}
