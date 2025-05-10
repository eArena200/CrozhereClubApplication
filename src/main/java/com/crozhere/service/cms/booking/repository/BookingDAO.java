package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.exception.BookingDAOException;

import java.util.List;

public interface BookingDAO {
    void save(Booking booking) throws BookingDAOException;
    Booking get(String bookingId) throws BookingDAOException;
    void update(String bookingId, Booking booking) throws BookingDAOException;
    void delete(String bookingId) throws BookingDAOException;

    List<Booking> getBookingByPlayerId(String playerId) throws BookingDAOException;
    List<Booking> getBookingByClubAdminId(String clubAdminId) throws BookingDAOException;
}
