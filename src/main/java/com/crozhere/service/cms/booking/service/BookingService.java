package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByStationRequest;
import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByTimeRequest;
import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;

import java.util.List;

public interface BookingService {

    Booking createBooking(CreateBookingRequest createBookingRequest)
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
