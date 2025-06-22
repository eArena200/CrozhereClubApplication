package com.crozhere.service.cms.booking.repository.dao.impl;

import com.crozhere.service.cms.booking.repository.BookingIntentRepository;
import com.crozhere.service.cms.booking.repository.dao.BookingIntentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingIntentDaoException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.club.repository.entity.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("BookingIntentSqlDao")
public class BookingIntentDaoImpl implements BookingIntentDao {

    private final BookingIntentRepository repository;

    @Autowired
    public BookingIntentDaoImpl(BookingIntentRepository repository) {
        this.repository = repository;
    }

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
    public List<BookingIntent> getActiveBookingIntentsForStationsForSearchWindow(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime) throws BookingIntentDaoException {
        try {
            List<Long> stationIds = stations.stream().map(Station::getId).toList();
            return repository.findActiveOverlappingIntents(stationIds, startTime, endTime, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error fetching active booking intents", e);
            throw new BookingIntentDaoException("QueryException", e);
        }
    }

    @Override
    public List<BookingIntent> getExpiredUnconfirmedIntents(LocalDateTime beforeTime) throws BookingIntentDaoException {
        try {
            return repository.findByIsConfirmedFalseAndExpiresAtBefore(beforeTime);
        } catch (Exception e) {
            log.error("Error fetching expired unconfirmed booking intents", e);
            throw new BookingIntentDaoException("QueryException", e);
        }
    }
}
