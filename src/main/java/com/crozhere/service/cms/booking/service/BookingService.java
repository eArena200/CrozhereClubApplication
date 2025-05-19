package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByStationRequest;
import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByTimeRequest;
import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;

import java.util.List;

public interface BookingService {

    Booking createBooking(CreateBookingRequest createBookingRequest) throws BookingServiceException;
    Booking getBookingById(String bookingId) throws BookingServiceException;
    Booking cancelBooking(String bookingId) throws BookingServiceException;

    List<Booking> listBookingByPlayerId(String playerId) throws BookingServiceException;
    List<Booking> listBookingByClubAdminId(String clubAdminId) throws BookingServiceException;

    BookingAvailabilityByTimeResponse checkAvailabilityByTime(
            BookingAvailabilityByTimeRequest bookingAvailabilityByTimeRequest) throws BookingServiceException;

    BookingAvailabilityByStationResponse checkAvailabilityByStations(
            BookingAvailabilityByStationRequest bookingAvailabilityByStationRequest) throws BookingServiceException;
}
