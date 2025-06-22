package com.crozhere.service.cms.booking.repository.dao;

import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingIntentDaoException;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.club.repository.entity.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingIntentDao {

    void save(BookingIntent bookingIntent) throws BookingIntentDaoException;

    Optional<BookingIntent> findById(Long intentId) throws BookingIntentDaoException;

    BookingIntent getById(Long intentId) throws DataNotFoundException, BookingIntentDaoException;

    void update(Long intentId, BookingIntent bookingIntent) throws DataNotFoundException, BookingIntentDaoException;

    void deleteById(Long intentId) throws BookingIntentDaoException;

    List<BookingIntent> getActiveBookingIntentsForStationsForSearchWindow(
            List<Station> stations, LocalDateTime startTime, LocalDateTime endTime)
            throws BookingIntentDaoException;

    List<BookingIntent> getExpiredUnconfirmedIntents(LocalDateTime beforeTime) throws BookingIntentDaoException;
}
