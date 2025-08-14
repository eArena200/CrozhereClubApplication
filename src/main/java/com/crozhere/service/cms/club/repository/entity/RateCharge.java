package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_charge")
public class RateCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_id", nullable = false)
    @ToString.Exclude
    private Rate rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type", nullable = false)
    private ChargeType chargeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private ChargeUnit unit;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "min_players")
    private Integer minPlayers;

    @Column(name = "max_players")
    private Integer maxPlayers;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt  = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Boolean isRateChargeApplicable(Instant currentInstant, Integer playerCount) {
        if (playerCount < this.minPlayers || playerCount > this.maxPlayers) {
            return false;
        }

        if (startTime == null || endTime == null) {
            return true;
        }

        LocalTime currentTime = LocalTime.ofInstant(currentInstant, ZoneOffset.UTC);

        if (startTime.isBefore(endTime)) {
            return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime);
        } else {
            return !currentTime.isBefore(startTime) || currentTime.isBefore(endTime);
        }
    }

}
