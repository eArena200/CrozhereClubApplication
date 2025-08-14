package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "station")
public class Station {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Club club;

    @ManyToOne
    @JoinColumn(name = "rate_id", nullable = false)
    private Rate rate;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "station_description")
    private String stationDescription;

    @Column(name = "station_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StationType stationType;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "station_capacity")
    private Integer capacity;

    @Column(name = "is_live", nullable = false)
    private Boolean isLive;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        if (this.capacity == null || this.capacity < 1) {
            this.capacity = 1;
        }

        if (this.isLive == null) {
            this.isLive = false;
        }

        if (this.isActive == null) {
            this.isActive = true;
        }

        if(this.stationDescription == null){
            this.stationDescription = "Specifications";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
