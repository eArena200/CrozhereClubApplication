package com.crozhere.service.cms.booking.repository.dao.impl;

import com.crozhere.service.cms.booking.repository.BookingRepository;
import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import com.crozhere.service.cms.booking.repository.entity.BookingType;
import com.crozhere.service.cms.booking.repository.specification.BookingSpecifications;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.entity.StationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookingDaoImpl implements BookingDao {

    private final BookingRepository bookingRepository;

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
            return bookingRepository.findByPlayerId(playerId);
        } catch (Exception e) {
            log.error("Exception while fetching bookings for playerId: {}", playerId, e);
            throw new BookingDAOException("GetBookingByPlayerIdException", e);
        }
    }

    @Override
    public Page<Booking> getBookingsByClubIdWithFilters(
            Long clubId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime,
            Set<StationType> stationTypes,
            Set<BookingStatus> bookingStatuses,
            Set<BookingType> bookingTypes,
            Pageable pageable
    ) throws BookingDAOException {
        try {
            var spec = BookingSpecifications.filterBookings(
                    clubId, fromDateTime, toDateTime, stationTypes, bookingStatuses, bookingTypes);

            return bookingRepository.findAll(spec, pageable);
        } catch (Exception e) {
            log.error("Exception while fetching filtered bookings for clubId: {}", clubId, e);
            throw new BookingDAOException("Error fetching filtered bookings", e);
        }
    }


    @Override
    public List<Booking> getBookingsForStationsAndForSearchWindow(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime)
            throws BookingDAOException {
        try {
            List<Long> stationIds = stations.stream().map(Station::getId).toList();
            return bookingRepository.findBookingsForStationForSearchWindow(stationIds, startTime, endTime);
        } catch (Exception e) {
            log.error("Failed to fetch overlapping bookings", e);
            throw new BookingDAOException("GetOverlappingBookingsException", e);
        }
    }

}
