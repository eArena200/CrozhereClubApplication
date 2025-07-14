package com.crozhere.service.cms.booking.repository.entity;

import com.crozhere.service.cms.club.repository.entity.StationType;
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
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_intent_id", nullable = false)
    private Long bookingIntentId;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false)
    private StationType stationType;

    @ElementCollection
    @CollectionTable(
            name = "booking_stations",
            joinColumns = @JoinColumn(name = "booking_id")
    )
    @Column(name = "station_id", nullable = false)
    private List<BookingStation> stations;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "booking_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "player_count", nullable = false)
    private Integer playersCount;
}
