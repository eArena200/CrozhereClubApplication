package com.crozhere.service.cms.booking.repository.entity;

import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.user.repository.entity.Player;
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
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "club_Id", nullable = false)
    private Long clubId;

    @ManyToMany
    @JoinTable(
            name = "booking_station",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private List<Station> stations;

    @Column(name = "booking_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

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
