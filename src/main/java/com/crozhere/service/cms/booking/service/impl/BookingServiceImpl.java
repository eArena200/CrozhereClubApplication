package com.crozhere.service.cms.booking.service.impl;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import com.crozhere.service.cms.auth.service.UserService;
import com.crozhere.service.cms.booking.controller.model.request.*;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.controller.model.response.StationAvailability;
import com.crozhere.service.cms.booking.repository.dao.BookingIntentDao;
import com.crozhere.service.cms.booking.repository.dao.PaymentDao;
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
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingIntentDao bookingIntentDao;
    private final BookingDao bookingDAO;
    private final PaymentDao paymentDao;
    private final BookingManager bookingManager;
    private final ClubService clubService;
    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(
            BookingDao bookingDAO,
            PaymentDao paymentDao,
            BookingIntentDao bookingIntentDao,
            BookingManager bookingManager,
            ClubService clubService,
            UserService userService,
            PlayerService playerService){
        this.bookingIntentDao = bookingIntentDao;
        this.bookingDAO = bookingDAO;
        this.paymentDao = paymentDao;
        this.bookingManager = bookingManager;
        this.userService = userService;
        this.clubService = clubService;
        this.playerService = playerService;
    }

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

            List<Station> stations = validateAndGetRequestStations(request);

            validateStationAvailability(request);

            BookingIntent bookingIntent = BookingIntent.builder()
                    .playerId(player.getId())
                    .clubId(request.getClubId())
                    .stations(stations)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .playerCount(request.getPlayers())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .isConfirmed(false)
                    .build();

            bookingIntentDao.save(bookingIntent);
            return bookingIntent;
        } catch (InvalidRequestException e) {
            log.error("Exception while creating booking-intent for request: {}", request);
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
                throw new BookingServiceException(BookingServiceExceptionType.GET_BOOKING_INTENT_FAILED);
            }

            if(intent.getExpiresAt().isBefore(LocalDateTime.now())){
                log.info("Booking-Intent confirmation period expired");
                throw new BookingServiceException(BookingServiceExceptionType.GET_BOOKING_INTENT_FAILED);
            }

            intent.setConfirmed(true);
            bookingIntentDao.save(intent);

            Payment payment = paymentDao.getById(request.getPaymentId());
            Player player = playerService.getPlayerById(intent.getPlayerId());
            Booking booking = Booking.builder()
                    .player(player)
                    .payment(payment)
                    .clubId(intent.getClubId())
                    .stations(intent.getStations())
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
    public List<Booking> listBookingByClubId(Long clubId)
            throws BookingServiceException {
        try {
            return bookingDAO.getBookingByClubId(clubId);
        } catch (BookingDAOException e) {
            log.error("Failed to list bookings for clubAdminId: {}", clubId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.LIST_BOOKINGS_BY_CLUB_FAILED);
        }
    }

    @Override
    public BookingAvailabilityByTimeResponse checkAvailabilityByTime(
            BookingAvailabilityByTimeRequest request)
            throws BookingServiceException {
        try {
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

    // TODO: Implement booking durations should be a multiple of an hour
    private List<Station> validateAndGetRequestStations(CreateBookingIntentRequest request)
            throws InvalidRequestException {
        try {
            if (request.getStationIds() == null || request.getStationIds().isEmpty()) {
                throw new InvalidRequestException("At least one station is required for booking");
            }

            List<Station> allowedStations = clubService.getStationsByClubIdAndType(
                    request.getClubId(), request.getStationType());

            List<Long> allowedStationIds = allowedStations.stream()
                    .map(Station::getId)
                    .toList();

            for (Long id : request.getStationIds()) {
                if (!allowedStationIds.contains(id)) {
                    log.error("StationId {} not found for clubId {}", id, request.getClubId());
                    throw new InvalidRequestException("InvalidStationId: " + id);
                }
            }

            return allowedStations.stream()
                    .filter(station -> request.getStationIds().contains(station.getId()))
                    .toList();

        } catch (ClubServiceException e) {
            log.error("ClubId {} not found or invalid", request.getClubId(), e);
            throw new InvalidRequestException("InvalidClubId: " + request.getClubId());
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
