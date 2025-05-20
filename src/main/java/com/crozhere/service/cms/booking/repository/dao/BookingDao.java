package com.crozhere.service.cms.booking.repository.dao;

import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.club.repository.entity.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao {
    void save(Booking booking) throws BookingDAOException;

    Optional<Booking> findById(Long bookingId) throws BookingDAOException;
    Booking getById(Long bookingId) throws DataNotFoundException, BookingDAOException;

    void update(Long bookingId, Booking booking) throws DataNotFoundException, BookingDAOException;

    void deleteById(Long bookingId) throws BookingDAOException;

    List<Booking> getBookingByPlayerId(Long playerId) throws BookingDAOException;
    List<Booking> getBookingByClubId(Long clubId) throws BookingDAOException;

    List<Booking> getOverlappingBookings(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime)
            throws BookingDAOException;

    List<Booking> getBookingsForStations(List<Station> stations) throws BookingDAOException;

}
