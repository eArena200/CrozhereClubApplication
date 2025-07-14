package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingIntentRepository extends JpaRepository<BookingIntent, Long> {

    @Query(value = """
        SELECT DISTINCT bi.* FROM booking_intent bi
        JOIN booking_intent_stations bis ON bi.id = bis.intent_id
        WHERE bis.station_id IN :stationIds
        AND bi.is_confirmed = false
        AND bi.is_cancelled = false
        AND bi.expires_at > :now
        AND bi.start_time < :endTime
        AND bi.end_time > :startTime
    """, nativeQuery = true)
    List<BookingIntent> findActiveOverlappingIntents(
            @Param("stationIds") List<Long> stationIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime")LocalDateTime endTime,
            @Param("now")LocalDateTime now
    );

    @Query(value = """
        SELECT bi.* FROM booking_intent bi
        WHERE bi.club_id = :clubId
        AND bi.is_cancelled = false
        AND bi.is_confirmed = false
        AND bi.expires_at > :now
    """, nativeQuery = true)
    List<BookingIntent> findActiveIntentsByClubId(
            @Param("clubId") Long clubId,
            @Param("now") LocalDateTime now
    );

    @Query(value = """
        SELECT bi.* FROM booking_intent bi
        WHERE bi.player_id = :playerId
        AND bi.is_cancelled = false
        AND bi.is_confirmed = false
        AND bi.expires_at > :now
    """, nativeQuery = true)
    List<BookingIntent> findActiveIntentsByPlayerId(
            @Param("playerId") Long playerId,
            @Param("now") LocalDateTime now
    );

    List<BookingIntent> findByIsConfirmedFalseAndExpiresAtBefore(LocalDateTime beforeTime);
}
