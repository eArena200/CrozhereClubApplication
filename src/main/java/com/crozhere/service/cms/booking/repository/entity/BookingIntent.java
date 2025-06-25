package com.crozhere.service.cms.booking.repository.entity;

import com.crozhere.service.cms.club.repository.entity.Station;
import jakarta.persistence.*;
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
@Entity
@Table(name = "booking_intent")
public class BookingIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_Id", nullable = false)
    private Long playerId;

    @Column(name = "club_Id", nullable = false)
    private Long clubId;

    @ManyToMany
    @JoinTable(
            name = "booking_intent_station",
            joinColumns = @JoinColumn(name = "intent_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private List<Station> stations;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "player_count", nullable = false)
    private Integer playerCount;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_confirmed", nullable = false)
    private boolean isConfirmed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
