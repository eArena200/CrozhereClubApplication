package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByStationRequest;
import com.crozhere.service.cms.booking.controller.model.request.BookingAvailabilityByTimeRequest;
import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByStationResponse;
import com.crozhere.service.cms.booking.controller.model.response.BookingAvailabilityByTimeResponse;
import com.crozhere.service.cms.booking.controller.model.response.StationAvailability;
import com.crozhere.service.cms.booking.repository.dao.PaymentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.entity.*;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDAO;
    private final PaymentDao paymentDao;
    private final BookingManager bookingManager;
    private final ClubService clubService;
    private final PlayerService playerService;

    @Autowired
    public BookingServiceImpl(
            @Qualifier("BookingSqlDao") BookingDao bookingDAO,
            @Qualifier("PaymentSqlDao") PaymentDao paymentDao,
            BookingManager bookingManager,
            ClubService clubService,
            PlayerService playerService){
        this.bookingDAO = bookingDAO;
        this.paymentDao = paymentDao;
        this.bookingManager = bookingManager;
        this.clubService = clubService;
        this.playerService = playerService;
    }

    @Override
    public Booking createBooking(CreateBookingRequest request)
            throws InvalidRequestException, BookingServiceException {
        try {
            validateRequest(request);
            // TODO: Validate player count according to the stationType
            validateAvailability(request);

            Payment payment = Payment.builder()
                    .status(PaymentStatus.SUCCESS)
                    .build();
            paymentDao.save(payment);

            Player player =
                    playerService.getPlayerById(request.getPlayerId());

            List<Station> stations = clubService
                    .getStationsByClubIdAndType(
                            request.getClubId(),
                            request.getStationType())
                    .stream()
                    .filter(station -> request.getStationIds().contains(station.getId()))
                    .toList();


            Booking booking = Booking.builder()
                    .player(player)
                    .stations(stations)
                    .bookingType(getBookingType(request.getPlayers()))
                    .payment(payment)
                    .status(BookingStatus.CONFIRMED)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .playersCount(request.getPlayers())
                    .build();
            bookingDAO.save(booking);
            return booking;
        } catch (InvalidRequestException | BookingServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BookingServiceException(
                    BookingServiceExceptionType.CREATE_BOOKING_FAILED);
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
    private void validateRequest(CreateBookingRequest request)
            throws InvalidRequestException {
        try {
            List<Long> possibleStationIds =
                    clubService.getStationsByClubIdAndType(
                            request.getClubId(), request.getStationType())
                            .stream().map(Station::getId).toList();

            for (Long id : request.getStationIds()) {
                if (!possibleStationIds.contains(id)) {
                    log.error("StationId {} not found for clubId {}", id, request.getClubId());
                    throw new InvalidRequestException("InvalidStationId:" + id);
                }
            }
        } catch (ClubServiceException e){
            log.error("ClubId {} not found", request.getClubId());
            throw new InvalidRequestException("InvalidClubId:" + request.getClubId());
        }
    }

    private void validateAvailability(CreateBookingRequest request)
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
        } catch (Exception e) {
            log.error("Error validating station availability", e);
            throw e;
        }
    }

    private BookingType getBookingType(Integer players){
        return players > 1 ? BookingType.GRP : BookingType.IND;
    }
}
