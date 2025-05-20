package com.crozhere.service.cms.booking.repository.dao.impl;

import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.common.IdSetters;
import com.crozhere.service.cms.common.InMemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("BookingInMemDao")
public class BookingInMemDao implements BookingDao {

    private final InMemRepository<Booking> bookingRepository =
            new InMemRepository<>(IdSetters.BOOKING_ID_SETTER);

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
    public Booking getById(Long bookingId) throws DataNotFoundException, BookingDAOException {
        try {
            return bookingRepository.getById(bookingId);
        } catch (NoSuchElementException e) {
            log.error("Booking not found with ID: {}", bookingId);
            throw new DataNotFoundException("Booking not found");
        } catch (Exception e) {
            log.error("Exception while getting booking with ID: {}", bookingId, e);
            throw new BookingDAOException("GetByIdException", e);
        }
    }

    @Override
    public void update(Long bookingId, Booking booking) throws DataNotFoundException, BookingDAOException {
        try {
            bookingRepository.update(bookingId, booking);
        } catch (NoSuchElementException e) {
            log.error("Booking not found for update with ID: {}", bookingId);
            throw new DataNotFoundException("Booking not found for update");
        } catch (Exception e) {
            log.error("Exception while updating booking with ID: {}", bookingId, e);
            throw new BookingDAOException("UpdateException", e);
        }
    }

    @Override
    public void deleteById(Long bookingId) throws BookingDAOException {
        try {
            bookingRepository.deleteById(bookingId);
        } catch (NoSuchElementException e) {
            log.error("Booking not found for delete with ID: {}", bookingId);
            throw new DataNotFoundException("Booking not found for delete");
        } catch (Exception e) {
            log.error("Exception while deleting booking with ID: {}", bookingId, e);
            throw new BookingDAOException("DeleteByIdException", e);
        }
    }

    @Override
    public List<Booking> getBookingByPlayerId(Long playerId) throws BookingDAOException {
        try {
            return bookingRepository.findAll().stream()
                    .filter(booking -> booking.getPlayer() != null
                            && booking.getPlayer().getId().equals(playerId))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get bookings by player ID: {}", playerId, e);
            throw new BookingDAOException("GetByPlayerIdException", e);
        }
    }

    @Override
    public List<Booking> getBookingByClubId(Long clubId) throws BookingDAOException {
        try {
            return bookingRepository.findAll().stream()
                    .filter(booking -> booking.getStations().stream()
                            .anyMatch(station ->
                                    station.getClub().getId().equals(clubId)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get bookings by clubAdmin ID: {}", clubId, e);
            throw new BookingDAOException("GetByClubAdminIdException", e);
        }
    }

    @Override
    public List<Booking> getOverlappingBookings(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime)
            throws BookingDAOException {
        try {
            List<Long> stationIds = stations.stream()
                    .map(Station::getId)
                    .toList();

            return bookingRepository.findAll().stream()
                    .filter(booking -> booking.getStations().stream()
                            .anyMatch(station -> stationIds.contains(station.getId())))
                    .filter(booking -> {
                        LocalDateTime bookingStart = booking.getStartTime();
                        LocalDateTime bookingEnd = booking.getEndTime();
                        return bookingStart.isBefore(endTime) && bookingEnd.isAfter(startTime);
                    }).toList();
        } catch (Exception e) {
            log.error("Failed to get overlapping bookings", e);
            throw new BookingDAOException("GetOverlappingBookingsException", e);
        }
    }

    @Override
    public List<Booking> getBookingsForStations(List<Station> stations)
            throws BookingDAOException {
        return List.of();
    }

}
