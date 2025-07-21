package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.response.*;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import com.crozhere.service.cms.booking.repository.entity.BookingType;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BookingService {

    // BOOKING-INTENT METHODS
    BookingIntentDetailsResponse createBookingIntentForClub(CreateClubBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException;

    BookingIntentDetailsResponse createBookingIntentForPlayer(CreatePlayerBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException;

    BookingIntent getBookingIntentById(Long bookingIntentId)
            throws BookingServiceException;

    List<BookingIntentDetailsResponse> getActiveIntentsForClub(Long clubId)
            throws BookingServiceException;

    List<BookingIntentDetailsResponse> getActiveIntentsForPlayer(Long playerId)
            throws BookingServiceException;

    void cancelClubBookingIntent(Long clubId, Long intentId)
            throws BookingServiceException;

    void cancelPlayerBookingIntent(Long playerId, Long intentId)
            throws BookingServiceException;

    // BOOKING METHODS
    BookingDetailsResponse confirmBookingIntent(ConfirmBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException;

    BookingDetailsResponse getClubBookingByIntentId(Long clubId, Long intentId)
            throws BookingServiceException;

    BookingDetailsResponse getPlayerBookingByIntentId(Long playerId, Long intentId)
            throws BookingServiceException;

    BookingDetailsResponse getClubBookingById(Long clubId, Long bookingId)
            throws BookingServiceException;

    BookingDetailsResponse getPlayerBookingById(Long playerId, Long bookingId)
            throws BookingServiceException;

    void cancelClubBooking(Long clubId, Long bookingId) throws BookingServiceException;

    void cancelPlayerBooking(Long playerId, Long bookingId) throws BookingServiceException;

    List<BookingDetailsResponse> listBookingByPlayerId(Long playerId) throws BookingServiceException;
    Page<BookingDetailsResponse> listBookingByClubIdWithFilters(
            Long clubId,
            ClubBookingsListFilterRequest filterRequest,
            Pageable pageable
    ) throws BookingServiceException;

    Map<Long, DashBoardStationStatusResponse> getDashboardStationStatusDetailsForClub(Long clubId)
            throws BookingServiceException;

    List<BookingDetailsResponse> getUpcomingBookingsByClubId(
            Long clubId,
            Long windowDurationHr,
            List<StationType> stationTypes
    ) throws BookingServiceException;

    BookingAvailabilityByTimeResponse checkAvailabilityByTime(
            BookingAvailabilityByTimeRequest bookingAvailabilityByTimeRequest)
            throws BookingServiceException;

    BookingAvailabilityByStationResponse checkAvailabilityByStations(
            BookingAvailabilityByStationRequest bookingAvailabilityByStationRequest)
            throws BookingServiceException;
}
