package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateChargeConstraint {
    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "min_players")
    private Integer minPlayers;

    @Column(name = "max_players")
    private Integer maxPlayers;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "rate_charge_constraint_days",
            joinColumns = @JoinColumn(name = "rate_charge_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    @Builder.Default
    private Set<DayOfWeek> applicableDays = new HashSet<>();
}
