package com.crozhere.service.cms.booking.repository.dao.impl;

import com.crozhere.service.cms.booking.repository.BookingIntentRepository;
import com.crozhere.service.cms.booking.repository.dao.BookingIntentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingIntentDaoException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.club.repository.entity.Station;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookingIntentDaoImpl implements BookingIntentDao {

    private final BookingIntentRepository repository;

    @Override
    public void save(BookingIntent bookingIntent) throws BookingIntentDaoException {
        try {
            repository.save(bookingIntent);
        } catch (Exception e) {
            log.error("Failed to save booking intent", e);
            throw new BookingIntentDaoException("SaveException", e);
        }
    }

    @Override
    public Optional<BookingIntent> findById(Long intentId) throws BookingIntentDaoException {
        try {
            return repository.findById(intentId);
        } catch (Exception e) {
            log.error("Failed to find booking intent by ID: {}", intentId, e);
            throw new BookingIntentDaoException("FindByIdException", e);
        }
    }

    @Override
    public BookingIntent getById(Long intentId) throws DataNotFoundException, BookingIntentDaoException {
        return findById(intentId)
                .orElseThrow(() -> new DataNotFoundException("BookingIntent not found: " + intentId));
    }

    @Override
    public List<BookingIntent> getIntentsByIds(List<Long> intentIds) throws BookingIntentDaoException {
        try {
            if (intentIds == null || intentIds.isEmpty()) {
                return List.of();
            }
            return repository.findAllById(intentIds);
        } catch (Exception e) {
            log.error("Failed to fetch booking intents by IDs: {}", intentIds, e);
            throw new BookingIntentDaoException("FindByIdsException", e);
        }
    }

    @Override
    public void update(Long intentId, BookingIntent bookingIntent) throws DataNotFoundException, BookingIntentDaoException {
        try {
            if (!repository.existsById(intentId)) {
                throw new DataNotFoundException("BookingIntent not found: " + intentId);
            }
            bookingIntent.setId(intentId);
            repository.save(bookingIntent);
        } catch (Exception e) {
            log.error("Failed to update booking intent", e);
            throw new BookingIntentDaoException("UpdateException", e);
        }
    }

    @Override
    public void deleteById(Long intentId) throws BookingIntentDaoException {
        try {
            repository.deleteById(intentId);
        } catch (Exception e) {
            log.error("Failed to delete booking intent by ID: {}", intentId, e);
            throw new BookingIntentDaoException("DeleteByIdException", e);
        }
    }

    @Override
    public List<BookingIntent> getActiveIntentsForStationsAndForSearchWindow(
            List<Station> stations, Instant startTime, Instant endTime
    ) throws BookingIntentDaoException {
        try {
            List<Long> stationIds = stations.stream().map(Station::getId).toList();
            return repository.findActiveOverlappingIntents(stationIds, startTime, endTime, Instant.now());
        } catch (Exception e) {
            log.error("Error fetching active booking intents", e);
            throw new BookingIntentDaoException("QueryException", e);
        }
    }

    @Override
    public List<BookingIntent> getExpiredUnconfirmedIntents(Instant beforeTime)
            throws BookingIntentDaoException {
        try {
            return repository.findByIsConfirmedFalseAndExpiresAtBefore(beforeTime);
        } catch (Exception e) {
            log.error("Error fetching expired unconfirmed booking intents", e);
            throw new BookingIntentDaoException("QueryException", e);
        }
    }

    @Override
    public List<BookingIntent> getActiveIntentsForClubId(Long clubId, Instant now)
            throws BookingIntentDaoException {
        try {
            return repository.findActiveIntentsByClubId(clubId, now);
        } catch (Exception e) {
            log.error("Failed to fetch active intents for clubId: {}", clubId, e);
            throw new BookingIntentDaoException("Error while fetching active booking intents", e);
        }
    }

    @Override
    public List<BookingIntent> getActiveIntentsForPlayerId(Long playerId, Instant now)
            throws BookingIntentDaoException {
        try {
            return repository.findActiveIntentsByPlayerId(playerId, now);
        } catch (Exception e){
            log.error("Failed to fetch active intents for playerId: {}", playerId, e);
            throw new BookingIntentDaoException("Error while fetching active booking intents", e);
        }
    }

}
