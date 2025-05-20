package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.club.repository.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPlayer_Id(Long playerId);

    @Query("""
        SELECT DISTINCT b FROM Booking b
        JOIN b.stations s
        WHERE s.club.id = :clubId
    """)
    List<Booking> findByClubId(@Param("clubId") Long clubId);

    @Query(
    """
        SELECT b FROM Booking b
        WHERE EXISTS (
            SELECT s FROM b.stations s
            WHERE s IN :stations
        )
        AND b.startTime < :endTime
        AND b.endTime > :startTime
    """)
    List<Booking> findBookingsForStationForSearchWindow(
            @Param("stations") List<Station> stations,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


}
