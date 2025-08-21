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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        defaultFill();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        defaultFill();
    }

    private void defaultFill(){
        if (this.capacity == null || this.capacity < 1) {
            this.capacity = 1;
        }

        if (this.isActive == null) {
            this.isActive = false;
        }

        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }
}
