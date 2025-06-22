package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingIntentRepository extends JpaRepository<BookingIntent, Long> {

    @Query("""
        SELECT DISTINCT bi FROM BookingIntent bi
        JOIN bi.stations s
        WHERE s.id IN :stationIds
        AND bi.isConfirmed = false
        AND bi.expiresAt > :now
        AND bi.startTime < :endTime
        AND bi.endTime > :startTime
    """)
    List<BookingIntent> findActiveOverlappingIntents(
            List<Long> stationIds,
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime now
    );

    List<BookingIntent> findByIsConfirmedFalseAndExpiresAtBefore(LocalDateTime beforeTime);
}
