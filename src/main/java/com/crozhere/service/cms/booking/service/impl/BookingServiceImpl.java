package com.crozhere.service.cms.booking.service.impl;

import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.repository.entity.UserRole;
import com.crozhere.service.cms.user.service.UserService;
import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.controller.model.response.StationAvailability;
import com.crozhere.service.cms.booking.repository.dao.BookingIntentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingIntentDaoException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.*;
import com.crozhere.service.cms.booking.service.BookingManager;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.booking.service.exception.BookingManagerException;
import com.crozhere.service.cms.booking.service.exception.BookingServiceExceptionType;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;
import com.crozhere.service.cms.booking.util.TimeSlot;
import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import com.crozhere.service.cms.user.repository.entity.Player;
import com.crozhere.service.cms.user.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingIntentDao bookingIntentDao;
    private final BookingDao bookingDAO;
    private final BookingManager bookingManager;

    private final ClubService clubService;
    private final PlayerService playerService;
    private final UserService userService;

    @Override
    @Transactional
    public BookingIntent createBookingIntent(UserRole creatorRole, CreateBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException {
        try {
            if (creatorRole == null) {
                log.error("Creator role can't be null");
                throw new InvalidRequestException("Creator role is required");
            }

            if (!StringUtils.hasText(request.getPlayerPhoneNumber())) {
                log.info("Player phone number is required for booking");
                throw new InvalidRequestException("PhoneNumber is required");
            }

            Player player;
            if (UserRole.PLAYER.equals(creatorRole)) {
                if (request.getPlayerId() == null) {
                    throw new InvalidRequestException("Player ID is required");
                }
                player = playerService.getPlayerById(request.getPlayerId());
            } else if (UserRole.CLUB_ADMIN.equals(creatorRole)) {
                User user = userService.getOrCreateUserByPhoneNumber(
                        request.getPlayerPhoneNumber(), UserRole.PLAYER);
                player = playerService.getPlayerByUserId(user.getId());
            } else {
                log.error("Unsupported role: {}", creatorRole);
                throw new InvalidRequestException("Unsupported creator role: " + creatorRole);
            }

            if(player == null){
                log.error("Cannot proceed booking without a player");
                throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED);
            }

            validateBookingTimes(request.getStartTime(), request.getEndTime());
            validateStations(request.getClubId(), request.getStationType(), request.getStationIds());
            validateStationAvailability(request);

            BookingIntent bookingIntent = BookingIntent.builder()
                    .playerId(player.getId())
                    .clubId(request.getClubId())
                    .stationType(request.getStationType())
                    .stationIds(request.getStationIds())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .playerCount(request.getPlayers())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .isConfirmed(false)
                    .totalCost(0.0)
                    .build();

            bookingIntentDao.save(bookingIntent);
            return bookingIntent;
        } catch (InvalidRequestException e) {
            log.error("Exception while creating booking-intent for request: {}", request, e);
            throw e;
        } catch (Exception e) {
            log.error("Error while creating booking intent for role {}, request: {}", creatorRole, request, e);
            throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED, e);
        }
    }


    @Override
    public BookingIntent getBookingIntentById(Long intentId) throws BookingServiceException {
        try {
            return bookingIntentDao.getById(intentId);
        } catch (DataNotFoundException e) {
            log.info("BookingIntent not found for intentId: {}", intentId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND);
        } catch (Exception e){
            log.error("Failed to fetch booking-intent for id: {}", intentId, e);
            throw new BookingServiceException(BookingServiceExceptionType.GET_BOOKING_INTENT_FAILED);
        }
    }

    @Override
    @Transactional
    public Booking confirmBookingIntent(ConfirmBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException {
        try {
            BookingIntent intent =
                    bookingIntentDao.getById(request.getBookingIntentId());

            if(intent.isConfirmed()){
                log.info("Booking-Intent already confirmed");
                throw new BookingServiceException(BookingServiceExceptionType.BOOKING_INTENT_ALREADY_USED);
            }

            if(intent.getExpiresAt().isBefore(LocalDateTime.now())){
                log.info("Booking-Intent confirmation period expired");
                throw new BookingServiceException(BookingServiceExceptionType.BOOKING_INTENT_EXPIRED);
            }

            intent.setConfirmed(true);
            bookingIntentDao.save(intent);

            Booking booking = Booking.builder()
                    .bookingIntent(intent)
                    .playerId(intent.getPlayerId())
                    .paymentId(request.getPaymentId())
                    .clubId(intent.getClubId())
                    .stationType(intent.getStationType())
                    .stationIds(new ArrayList<>(intent.getStationIds()))
                    .status(BookingStatus.CONFIRMED)
                    .playersCount(intent.getPlayerCount())
                    .bookingType(getBookingType(intent.getPlayerCount()))
                    .startTime(intent.getStartTime())
                    .endTime(intent.getEndTime())
                    .build();

            bookingDAO.save(booking);
            return booking;
        } catch (DataNotFoundException e){
            log.info("Booking-Intent not found for confirmation with id: {}",
                    request.getBookingIntentId());
            throw new BookingServiceException(BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND);
        } catch (BookingIntentDaoException e){
            log.error("Exception while fetching booking-intent for confirmation with id: {}",
                    request.getBookingIntentId());
            throw new BookingServiceException(BookingServiceExceptionType.CONFIRM_BOOKING_INTENT_FAILED);
        } catch (BookingDAOException e){
            log.error("Exception while saving new booking", e);
            throw new BookingServiceException(BookingServiceExceptionType.CONFIRM_BOOKING_INTENT_FAILED);
        } catch (BookingServiceException e){
            throw e;
        } catch (Exception e){
            log.error("Unknown exception while confirming booking-intent", e);
            throw new BookingServiceException(BookingServiceExceptionType.CONFIRM_BOOKING_INTENT_FAILED);
        }
    }

    @Override
    public Booking getBookingById(Long bookingId) throws BookingServiceException {
        try {
            return bookingDAO.getById(bookingId);
        } catch (DataNotFoundException e) {
            log.error("Booking not found with ID: {}", bookingId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingDAOException e) {
            log.error("Failed to fetch booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        }
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId) throws BookingServiceException {
        try {
            Booking booking = bookingDAO.getById(bookingId);
            booking.setStatus(BookingStatus.CANCELLED);
            bookingDAO.update(bookingId, booking);
            return booking;
        } catch (DataNotFoundException e) {
            log.error("Booking not found with ID {} for cancel", bookingId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingDAOException e) {
            log.error("Failed to cancel booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_FAILED);
        }
    }

    @Override
    public List<Booking> listBookingByPlayerId(Long playerId)
            throws BookingServiceException {
        try {
            return bookingDAO.getBookingByPlayerId(playerId);
        } catch (BookingDAOException e) {
            log.error("Failed to list bookings for playerId: {}", playerId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.LIST_BOOKINGS_BY_PLAYER_FAILED);
        }
    }

    @Override
    public Page<Booking> listBookingByClubIdWithFilters(
            Long clubId,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime,
            List<StationType> stationTypes,
            List<BookingStatus> bookingStatuses,
            List<BookingType> bookingTypes,
            Pageable pageable
    ) throws BookingServiceException {
        try {
            Set<StationType> stationTypeSet = stationTypes != null ? new HashSet<>(stationTypes): null;
            Set<BookingStatus> bookingStatusSet = bookingStatuses != null ? new HashSet<>(bookingStatuses) : null;
            Set<BookingType> bookingTypeSet = bookingTypes != null ? new HashSet<>(bookingTypes) : null;
            return bookingDAO.getBookingsByClubIdWithFilters(
                    clubId, fromDateTime, toDateTime,
                    stationTypeSet, bookingStatusSet, bookingTypeSet,
                    pageable);
        } catch (BookingDAOException e) {
            log.error("Failed to list bookings for clubId: {}", clubId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.LIST_BOOKINGS_BY_CLUB_FAILED);
        }
    }


    @Override
    public BookingAvailabilityByTimeResponse checkAvailabilityByTime(
            BookingAvailabilityByTimeRequest request)
            throws BookingServiceException {
        try {
            validateBookingTimes(request.getStartTime(), request.getEndTime());
            List<StationAvailability> availableStations =
                    bookingManager.getAvailableStationsForTime(
                            request.getClubId(),
                            request.getStationType(),
                            request.getStartTime(),
                            request.getEndTime());

            return BookingAvailabilityByTimeResponse.builder()
                    .clubId(request.getClubId())
                    .stationType(request.getStationType())
                    .stationsAvailability(availableStations)
                    .build();
        } catch (InvalidRequestException e) {
            log.error("Exception in checkAvailabilityByTime for request: {}", request, e);
            throw e;
        } catch (BookingManagerException e) {
            log.error("Exception in BookingManager in checkAvailabilityByTime", e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CHECK_AVAILABILITY_BY_TIME_FAILED);
        } catch (Exception e) {
            log.error("Unknown exception in checkAvailabilityByTime", e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CHECK_AVAILABILITY_BY_TIME_FAILED);
        }
    }

    @Override
    public BookingAvailabilityByStationResponse checkAvailabilityByStations(
            BookingAvailabilityByStationRequest request)
            throws BookingServiceException {
        try {
            validateStations(request.getClubId(), request.getStationType(), request.getStationIds());

            if (isNotAlignedTo30Minutes(request.getSearchWindow().getDateTime())){
                throw new InvalidRequestException("Search window should be multiple of 30 minutes");
            }

            if( request.getDurationHrs() < 1){
                throw new InvalidRequestException("Duration of booking should be minimum of 1 hour");
            }

            List<TimeSlot> availableTimeSlots =
                    bookingManager.getAvailableTimeSlotsForStations(
                            request.getClubId(),
                            request.getStationType(),
                            request.getStationIds(),
                            request.getDurationHrs(),
                            request.getSearchWindow());

            List<LocalDateTime> availableTimes =
                    availableTimeSlots.stream()
                            .map(TimeSlot::getStartTime).toList();

            return BookingAvailabilityByStationResponse.builder()
                    .clubId(request.getClubId())
                    .stationType(request.getStationType())
                    .stationIds(request.getStationIds())
                    .availableTimes(availableTimes)
                    .build();
        } catch (InvalidRequestException e) {
            log.error("Exception in checkAvailabilityByStation for request: {}", request, e);
            throw e;
        } catch (BookingManagerException e) {
            log.error("Exception in BookingManager in checkAvailabilityByStations", e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CHECK_AVAILABILITY_BY_STATIONS_FAILED);
        } catch (Exception e) {
            log.error("Unknown exception in checkAvailabilityByStations", e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CHECK_AVAILABILITY_BY_STATIONS_FAILED);
        }
    }



    private void validateBookingTimes(LocalDateTime startTime, LocalDateTime endTime)
            throws InvalidRequestException {
        if (startTime == null || endTime == null) {
            throw new InvalidRequestException("Start time and end time must be provided");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Start time must be in the present or future");
        }

        if (!startTime.isBefore(endTime)) {
            throw new InvalidRequestException("Start time must be before end time");
        }

        long minutesDiff = Duration.between(startTime, endTime).toMinutes();

        if (minutesDiff < 60) {
            throw new InvalidRequestException("Booking duration must be at least 1 hour");
        }

        if (minutesDiff % 30 != 0) {
            throw new InvalidRequestException("Booking duration must be a multiple of 30 minutes");
        }

        if (isNotAlignedTo30Minutes(startTime) || isNotAlignedTo30Minutes(endTime)) {
            throw new InvalidRequestException("Start and end time must be aligned to 30-minute intervals (e.g., 10:00, 10:30)");
        }
    }

    private boolean isNotAlignedTo30Minutes(LocalDateTime time) {
        int minute = time.getMinute();
        int second = time.getSecond();
        int nano = time.getNano();

        return !( (minute == 0 || minute == 30) && second == 0 && nano == 0 );
    }


    private void validateStations(Long clubId, StationType stationType, List<Long> stationIds)
            throws InvalidRequestException {
        try {
            if (clubId == null){
                throw new InvalidRequestException("Valid clubId is required");
            }

            if (stationIds == null || stationIds.isEmpty()) {
                throw new InvalidRequestException("At least one station is required for booking");
            }

            List<Station> allowedStations = clubService.getStationsByClubIdAndType(clubId, stationType);
            List<Long> allowedStationIds = allowedStations.stream()
                    .map(Station::getId)
                    .toList();

            for (Long id : stationIds) {
                if (!allowedStationIds.contains(id)) {
                    log.error("StationId {} not found for clubId {}", id, clubId);
                    throw new InvalidRequestException("InvalidStationId: " + id);
                }
            }
        } catch (ClubServiceException e) {
            log.error("Exception in clubService while creating booking-intent", e);
            throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED);
        }
    }

    private void validateStationAvailability(CreateBookingIntentRequest request)
            throws Exception {
        try {
            List<StationAvailability> available =
                    bookingManager.getAvailableStationsForTime(
                            request.getClubId(),
                            request.getStationType(),
                            request.getStartTime(),
                            request.getEndTime()
                    );

            Set<Long> availableIds = available.stream()
                    .filter(StationAvailability::isAvailable)
                    .map(StationAvailability::getStationId)
                    .collect(Collectors.toSet());

            for (Long id : request.getStationIds()) {
                if (!availableIds.contains(id)) {
                    log.error("StationId {} not available", id);
                    throw new BookingServiceException(
                            BookingServiceExceptionType.INVALID_AVAILABILITY);
                }
            }
        } catch (BookingServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating station availability", e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_INTENT_VALIDATION_FAILED, e);
        }
    }

    private BookingType getBookingType(Integer players){
        return players > 1 ? BookingType.GRP : BookingType.IND;
    }
}
