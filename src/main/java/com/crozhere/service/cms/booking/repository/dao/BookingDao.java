package com.crozhere.service.cms.booking.repository.dao;

import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import com.crozhere.service.cms.booking.repository.entity.BookingType;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.entity.StationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingDao {
    void save(Booking booking) throws BookingDAOException;

    Optional<Booking> findById(Long bookingId) throws BookingDAOException;
    Booking getById(Long bookingId) throws DataNotFoundException, BookingDAOException;

    Booking getByIntentId(Long intentId) throws DataNotFoundException, BookingDAOException;

    void update(Long bookingId, Booking booking) throws DataNotFoundException, BookingDAOException;

    void deleteById(Long bookingId) throws BookingDAOException;

    List<Booking> getBookingsByPlayerId(Long playerId) throws BookingDAOException;
    Page<Booking> getBookingsByClubIdWithFilters(
            Long clubId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime,
            Set<StationType> stationTypes,
            Set<BookingStatus> bookingStatuses,
            Set<BookingType> bookingTypes,
            Pageable pageable
    ) throws BookingDAOException;

    List<Booking> getBookingsForStationsAndForSearchWindow(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime)
            throws BookingDAOException;
}
