package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List<Booking> findByPlayerId(Long playerId);

    Optional<Booking> findByBookingIntentId(Long intentId);

    @Query(value = """
        SELECT DISTINCT b.* FROM booking b
        JOIN booking_stations bs ON b.id = bs.booking_id
        WHERE bs.station_id IN :stationIds
        AND b.start_time < :endTime
        AND b.end_time > :startTime
    """, nativeQuery = true)
    List<Booking> findBookingsForStationForSearchWindow(
            @Param("stationIds") List<Long> stationIds,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
}


