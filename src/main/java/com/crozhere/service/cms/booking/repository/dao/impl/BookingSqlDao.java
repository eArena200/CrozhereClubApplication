package com.crozhere.service.cms.booking.repository.dao.impl;

import com.crozhere.service.cms.booking.repository.BookingRepository;
import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.club.repository.entity.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("BookingSqlDao")
public class BookingSqlDao implements BookingDao {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingSqlDao(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void save(Booking booking) throws BookingDAOException {
        try {
            bookingRepository.save(booking);
        } catch (Exception e) {
            log.error("Failed to save booking: {}", booking, e);
            throw new BookingDAOException("SaveException", e);
        }
    }

    @Override
    public Optional<Booking> findById(Long bookingId) throws BookingDAOException {
        try {
            return bookingRepository.findById(bookingId);
        } catch (Exception e) {
            log.error("Exception in findById for bookingId: {}", bookingId, e);
            throw new BookingDAOException("FindByIdException", e);
        }
    }

    @Override
    public Booking getById(Long bookingId)
            throws DataNotFoundException, BookingDAOException {
        try {
            return bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new DataNotFoundException("Booking not found with ID: "
                            + bookingId));
        } catch (DataNotFoundException e) {
            log.error("Booking not found with ID: {}", bookingId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while getting booking with ID: {}", bookingId, e);
            throw new BookingDAOException("GetByIdException", e);
        }
    }

    @Override
    public void update(Long bookingId, Booking booking)
            throws DataNotFoundException, BookingDAOException {
        try {
            if (!bookingRepository.existsById(bookingId)) {
                log.error("Booking not found for update with ID: {}", bookingId);
                throw new DataNotFoundException("UpdateException");
            }
            booking.setId(bookingId);
            bookingRepository.save(booking);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating booking with ID: {}", bookingId, e);
            throw new BookingDAOException("UpdateException", e);
        }
    }

    @Override
    public void deleteById(Long bookingId) throws BookingDAOException {
        try {
            bookingRepository.deleteById(bookingId);
        } catch (Exception e) {
            log.error("Exception while deleting booking with ID: {}", bookingId, e);
            throw new BookingDAOException("DeleteByIdException", e);
        }
    }

    @Override
    public List<Booking> getBookingByPlayerId(Long playerId)
            throws BookingDAOException {
        try {
            return bookingRepository.findByPlayer_Id(playerId);
        } catch (Exception e) {
            log.error("Exception while fetching bookings for playerId: {}", playerId, e);
            throw new BookingDAOException("GetBookingByPlayerIdException", e);
        }
    }

    @Override
    public List<Booking> getBookingByClubId(Long clubId)
            throws BookingDAOException {
        try {
            return bookingRepository.findByClubId(clubId);
        } catch (Exception e) {
            log.error("Exception while fetching bookings for clubAdminId: {}", clubId, e);
            throw new BookingDAOException("GetBookingByClubAdminIdException", e);
        }
    }


    @Override
    public List<Booking> getBookingsForStationsForSearchWindow(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime)
            throws BookingDAOException {
        try {
            return bookingRepository.findBookingsForStationForSearchWindow(stations, startTime, endTime);
        } catch (Exception e) {
            log.error("Failed to fetch overlapping bookings", e);
            throw new BookingDAOException("GetOverlappingBookingsException", e);
        }
    }

}
