package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByStationRequest;
import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByTimeRequest;
import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDAO;

    @Autowired
    public BookingServiceImpl(
            @Qualifier("BookingInMemDao") BookingDao bookingDAO){
        this.bookingDAO = bookingDAO;
    }

    @Override
    public Booking createBooking(CreateBookingRequest createBookingRequest)
            throws BookingServiceException {
        return null;
    }

    @Override
    public Booking getBookingById(String bookingId)
            throws BookingServiceException {
        return null;
    }

    @Override
    public Booking cancelBooking(String bookingId)
            throws BookingServiceException {
        return null;
    }

    @Override
    public List<Booking> listBookingByPlayerId(String playerId)
            throws BookingServiceException {
        return List.of();
    }

    @Override
    public List<Booking> listBookingByClubAdminId(String clubAdminId)
            throws BookingServiceException {
        return List.of();
    }

    @Override
    public BookingAvailabilityByTimeResponse checkAvailabilityByTime(
            BookingAvailabilityByTimeRequest bookingAvailabilityByTimeRequest)
            throws BookingServiceException {
        return null;
    }

    @Override
    public BookingAvailabilityByStationResponse checkAvailabilityByStations(
            BookingAvailabilityByStationRequest bookingAvailabilityByStationRequest)
            throws BookingServiceException {
        return null;
    }
}
