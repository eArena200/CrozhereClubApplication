package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.user.repository.entity.UserRole;
import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;

import java.util.List;

public interface BookingService {
    BookingIntent createBookingIntent(UserRole creatorRole, CreateBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException;

    BookingIntent getBookingIntentById(Long intentId)
            throws BookingServiceException;

    Booking confirmBookingIntent(ConfirmBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException;

    Booking getBookingById(Long bookingId) throws BookingServiceException;
    Booking cancelBooking(Long bookingId) throws BookingServiceException;

    List<Booking> listBookingByPlayerId(Long playerId) throws BookingServiceException;
    List<Booking> listBookingByClubId(Long clubId) throws BookingServiceException;

    BookingAvailabilityByTimeResponse checkAvailabilityByTime(
            BookingAvailabilityByTimeRequest bookingAvailabilityByTimeRequest) throws BookingServiceException;

    BookingAvailabilityByStationResponse checkAvailabilityByStations(
            BookingAvailabilityByStationRequest bookingAvailabilityByStationRequest) throws BookingServiceException;
}
