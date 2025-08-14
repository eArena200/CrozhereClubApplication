package com.crozhere.service.cms.booking.repository.entity;

import com.crozhere.service.cms.club.repository.entity.StationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_intent")
public class BookingIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false)
    private StationType stationType;

    @ElementCollection
    @CollectionTable(name = "booking_intent_stations", joinColumns = @JoinColumn(name = "intent_id"))
    @Column(name = "station_id")
    private List<BookingIntentStation> stations;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "player_count", nullable = false)
    private Integer playerCount;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled;

    @Column(name = "is_confirmed", nullable = false)
    @Builder.Default
    private Boolean isConfirmed = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private BookingIntentMode intentMode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_amount_id")
    private BookingAmount bookingAmount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
