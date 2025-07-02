package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List<Booking> findByPlayerId(Long playerId);

    @Query(value = """
        SELECT DISTINCT b.* FROM booking b
        JOIN booking_station_ids bs ON b.id = bs.booking_id
        WHERE bs.station_id IN :stationIds
        AND b.start_time < :endTime
        AND b.end_time > :startTime
    """, nativeQuery = true)
    List<Booking> findBookingsForStationForSearchWindow(
            @Param("stationIds") List<Long> stationIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}


