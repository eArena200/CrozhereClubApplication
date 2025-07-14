package com.crozhere.service.cms.booking.service.impl;

import com.crozhere.service.cms.booking.controller.model.response.BookingIntentClubDetails;
import com.crozhere.service.cms.booking.controller.model.response.BookingIntentPlayerDetails;
import com.crozhere.service.cms.booking.controller.model.response.BookingIntentStationDetails;
import com.crozhere.service.cms.booking.controller.model.response.*;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.repository.entity.UserRole;
import com.crozhere.service.cms.user.service.UserService;
import com.crozhere.service.cms.booking.controller.model.request.*;
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
import com.crozhere.service.cms.user.repository.entity.Player;
import com.crozhere.service.cms.user.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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

    private final Integer INTENT_EXPIRATION_INTERVAL_MIN = 10;

    @Override
    @Transactional
    public BookingIntentDetailsResponse createBookingIntentForClub(CreateClubBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException {
        try {
            if (!StringUtils.hasText(request.getPlayerPhoneNumber())) {
                log.info("Player phone number is required for booking");
                throw new InvalidRequestException("PhoneNumber is required");
            }

            User user = userService.getOrCreateUserByPhoneNumber(
                    request.getPlayerPhoneNumber(), UserRole.PLAYER);
            Player player = playerService.getPlayerByUserId(user.getId());

            if(player == null){
                log.error("Cannot proceed club booking without a player");
                throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED);
            }

            validateBookingTimes(request.getStartTime(), request.getEndTime());

            Club club = clubService.getClubById(request.getClubId());
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubIdAndType(
                            request.getClubId(), request.getStationType())
                            .stream()
                            .collect(Collectors.toMap(
                                Station::getId,
                                Function.identity()
                            ));

            validateStations(stationMap , request.getStations());

            validateStationAvailability(
                    request.getClubId(),
                    request.getStationType(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getStations()
            );

            List<BookingIntentStation> bookingIntentStations = request.getStations()
                    .stream()
                    .map(s -> BookingIntentStation.builder()
                            .stationId(s.getStationId())
                            .playerCount(s.getPlayerCount())
                            .build())
                    .toList();

            BookingIntent bookingIntent = BookingIntent.builder()
                    .playerId(player.getId())
                    .clubId(request.getClubId())
                    .stationType(request.getStationType())
                    .stations(bookingIntentStations)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .playerCount(
                            request.getStations().stream()
                                    .mapToInt(BookingStationRequest::getPlayerCount)
                                    .sum()
                    )
                    .expiresAt(
                            LocalDateTime.now()
                                    .plusMinutes(INTENT_EXPIRATION_INTERVAL_MIN)
                    )
                    .isCancelled(false)
                    .isConfirmed(false)
                    .totalCost(0.0)
                    .intentMode(BookingIntentMode.OFFLINE)
                    .build();

            bookingIntentDao.save(bookingIntent);
            return getBookingIntentResponse(bookingIntent, player, club, stationMap);

        } catch (InvalidRequestException e) {
            log.error("Exception while creating booking-intent for request: {}", request, e);
            throw e;
        } catch (Exception e) {
            log.error("Error while creating booking-intent for request: {}", request, e);
            throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED, e);
        }
    }

    @Override
    public BookingIntentDetailsResponse createBookingIntentForPlayer(CreatePlayerBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException {
        try {
            if (request.getPlayerId() == null) {
                log.info("PlayerId is required for booking");
                throw new InvalidRequestException("PhoneNumber is required");
            }

            Player player = playerService.getPlayerById(request.getPlayerId());
            if(player == null){
                log.error("Cannot proceed player booking without a player");
                throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED);
            }

            validateBookingTimes(request.getStartTime(), request.getEndTime());

            Club club = clubService.getClubById(request.getClubId());
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubIdAndType(
                                    request.getClubId(), request.getStationType())
                            .stream()
                            .collect(Collectors.toMap(
                                    Station::getId,
                                    Function.identity()
                            ));

            validateStations(stationMap, request.getStations());

            validateStationAvailability(
                    request.getClubId(),
                    request.getStationType(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getStations()
            );

            List<BookingIntentStation> bookingIntentStations = request.getStations()
                    .stream()
                    .map(s -> BookingIntentStation.builder()
                            .stationId(s.getStationId())
                            .playerCount(s.getPlayerCount())
                            .build())
                    .toList();

            BookingIntent bookingIntent = BookingIntent.builder()
                    .playerId(player.getId())
                    .clubId(request.getClubId())
                    .stationType(request.getStationType())
                    .stations(bookingIntentStations)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .playerCount(
                            request.getStations().stream()
                                    .mapToInt(BookingStationRequest::getPlayerCount)
                                    .sum()
                    )
                    .expiresAt(
                            LocalDateTime.now()
                                    .plusMinutes(INTENT_EXPIRATION_INTERVAL_MIN)
                    )
                    .isCancelled(false)
                    .isConfirmed(false)
                    .totalCost(0.0)
                    .intentMode(BookingIntentMode.ONLINE)
                    .build();

            bookingIntentDao.save(bookingIntent);
            return getBookingIntentResponse(bookingIntent, player, club, stationMap);
        } catch (InvalidRequestException e) {
            log.error("Exception while creating booking-intent for request: {}", request, e);
            throw e;
        } catch (Exception e) {
            log.error("Error while creating booking-intent for request: {}", request, e);
            throw new BookingServiceException(BookingServiceExceptionType.CREATE_BOOKING_INTENT_FAILED, e);
        }
    }

    @Override
    public BookingIntent getBookingIntentById(Long bookingIntentId) throws BookingServiceException {
        try {
            return bookingIntentDao.getById(bookingIntentId);
        } catch (BookingIntentDaoException e){
            log.error("Exception while getting booking-intent for intentId: {}", bookingIntentId, e);
            throw new BookingServiceException(BookingServiceExceptionType.GET_BOOKING_INTENT_FAILED);
        }
    }

    @Override
    public List<BookingIntentDetailsResponse> getActiveIntentsForClub(Long clubId)
            throws BookingServiceException {

        try {
            Club club = clubService.getClubById(clubId);
            LocalDateTime now = LocalDateTime.now();

            List<BookingIntent> activeIntents =
                    bookingIntentDao.getActiveIntentsForClubId(clubId, now);

            if (activeIntents.isEmpty()) {
                return List.of();
            }

            List<Long> playerIds = activeIntents.stream()
                    .map(BookingIntent::getPlayerId)
                    .distinct()
                    .toList();

            Map<Long, Player> playerMap = playerService.getPlayersByIds(playerIds).stream()
                    .collect(Collectors.toMap(Player::getId, Function.identity()));

            Map<Long, Station> stationMap = clubService.getStationsByClubId(clubId).stream()
                    .collect(Collectors.toMap(Station::getId, Function.identity()));

            return activeIntents.stream()
                    .map(intent -> getBookingIntentResponse(
                            intent,
                            playerMap.get(intent.getPlayerId()),
                            club,
                            stationMap))
                    .toList();

        } catch (Exception e) {
            log.error("Error fetching active booking intents for clubId: {}", clubId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_ACTIVE_INTENTS_FAILED);
        }
    }


    @Override
    public List<BookingIntentDetailsResponse> getActiveIntentsForPlayer(Long playerId)
            throws BookingServiceException {

        try {
            LocalDateTime now = LocalDateTime.now();
            List<BookingIntent> activeIntents =
                    bookingIntentDao.getActiveIntentsForPlayerId(playerId, now);

            if (activeIntents.isEmpty()) {
                return List.of();
            }

            Player player = playerService.getPlayerById(playerId);

            List<Long> clubIds = activeIntents.stream()
                    .map(BookingIntent::getClubId)
                    .distinct()
                    .toList();

            Map<Long, Club> clubMap = clubService.getClubsByIds(clubIds).stream()
                    .collect(Collectors.toMap(Club::getId, Function.identity()));

            Map<Long, Station> stationMap = clubService.getStationsByClubIds(clubIds).stream()
                    .collect(Collectors.toMap(Station::getId, Function.identity()));

            return activeIntents.stream()
                    .map(intent -> getBookingIntentResponse(
                            intent,
                            player,
                            clubMap.get(intent.getClubId()),
                            stationMap))
                    .toList();

        } catch (Exception e) {
            log.error("Error fetching active booking intents for playerId: {}", playerId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_ACTIVE_INTENTS_FAILED);
        }
    }


    @Override
    @Transactional
    public void cancelClubBookingIntent(Long clubId, Long intentId)
            throws BookingServiceException {
        try {
            BookingIntent intent = bookingIntentDao.findById(intentId)
                    .orElseThrow(() -> new BookingServiceException(
                            BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND));

            if (!intent.getClubId().equals(clubId)
                    || !BookingIntentMode.OFFLINE.equals(intent.getIntentMode())) {
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND);
            }

            if (intent.getIsCancelled()) {
                return;
            }

            if (intent.isConfirmed()) {
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_INTENT_NOT_CANCELLABLE);
            }

            intent.setIsCancelled(true);
            bookingIntentDao.save(intent);
        } catch (BookingServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while cancelling booking intent with ID {}", intentId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_INTENT_FAILED);
        }
    }

    @Override
    @Transactional
    public void cancelPlayerBookingIntent(Long playerId, Long intentId)
            throws BookingServiceException {
        try {
            BookingIntent intent = bookingIntentDao.findById(intentId)
                    .orElseThrow(() -> new BookingServiceException(
                            BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND));

            if (!intent.getPlayerId().equals(playerId)
                    || !BookingIntentMode.ONLINE.equals(intent.getIntentMode())) {
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND);
            }

            if (intent.getIsCancelled()) {
                return;
            }

            if (intent.isConfirmed()) {
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_INTENT_NOT_CANCELLABLE);
            }

            intent.setIsCancelled(true);
            bookingIntentDao.save(intent);

        } catch (BookingServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while cancelling booking intent with ID {}", intentId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_INTENT_FAILED);
        }
    }

    @Override
    @Transactional
    public BookingDetailsResponse confirmBookingIntent(ConfirmBookingIntentRequest request)
            throws InvalidRequestException, BookingServiceException {
        try {
            BookingIntent intent =
                    bookingIntentDao.getById(request.getBookingIntentId());

            if(intent.getIsCancelled()){
                log.info("Booking-Intent already cancelled");
                throw new BookingServiceException(BookingServiceExceptionType.BOOKING_INTENT_ALREADY_USED);
            }

            if(intent.isConfirmed()){
                log.info("Booking-Intent already confirmed");
                throw new BookingServiceException(BookingServiceExceptionType.BOOKING_INTENT_ALREADY_USED);
            }

            if(intent.getExpiresAt().isBefore(LocalDateTime.now())){
                log.info("Booking-Intent confirmation period expired");
                // TODO: Refund process should start here.
                throw new BookingServiceException(BookingServiceExceptionType.BOOKING_INTENT_EXPIRED);
            }

            intent.setConfirmed(true);
            bookingIntentDao.save(intent);

            Booking booking = Booking.builder()
                    .bookingIntentId(intent.getId())
                    .playerId(intent.getPlayerId())
                    .paymentId(request.getPaymentId())
                    .clubId(intent.getClubId())
                    .stationType(intent.getStationType())
                    .stations(
                        intent.getStations().stream()
                            .map(station -> BookingStation.builder()
                                .stationId(station.getStationId())
                                .playerCount(station.getPlayerCount())
                                .build())
                            .toList())
                    .status(BookingStatus.CONFIRMED)
                    .playersCount(intent.getPlayerCount())
                    .bookingType(getBookingType(intent.getPlayerCount()))
                    .startTime(intent.getStartTime())
                    .endTime(intent.getEndTime())
                    .build();

            bookingDAO.save(booking);
            Player player = playerService.getPlayerById(booking.getPlayerId());
            Club club = clubService.getClubById(booking.getClubId());
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubId(intent.getClubId())
                            .stream()
                            .collect(Collectors.toMap(
                                    Station::getId,
                                    Function.identity()));
            return getBookingResponse(booking, intent, player, club, stationMap);
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
    public BookingDetailsResponse getClubBookingByIntentId(Long clubId, Long intentId)
        throws BookingServiceException {
        try {
            BookingIntent intent = bookingIntentDao.getById(intentId);
            if(!intent.getClubId().equals(clubId)
                    || !BookingIntentMode.OFFLINE.equals(intent.getIntentMode())){
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_NOT_FOUND);
            }
            Booking booking = bookingDAO.getByIntentId(intentId);
            Player player = playerService.getPlayerById(intent.getPlayerId());
            Club club = clubService.getClubById(intent.getClubId());
            Map<Long, Station> stationMap =
                clubService.getStationsByClubId(intent.getClubId())
                    .stream()
                    .collect(Collectors.toMap(
                        Station::getId,
                        Function.identity()));

            return getBookingResponse(booking, intent, player, club, stationMap);
        } catch (DataNotFoundException e) {
            log.error("Booking not found for intentId: {}", intentId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingIntentDaoException e) {
            log.error("Failed to fetch booking-intent with intentId: {}", intentId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        } catch (BookingDAOException e) {
            log.error("Failed to fetch booking with intentId: {}", intentId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        }
    }

    @Override
    public BookingDetailsResponse getPlayerBookingByIntentId(Long playerId, Long intentId)
            throws BookingServiceException {
        try {
            BookingIntent intent = bookingIntentDao.getById(intentId);
            if(!intent.getPlayerId().equals(playerId)
                    || !BookingIntentMode.ONLINE.equals(intent.getIntentMode())){
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_NOT_FOUND);
            }
            Booking booking = bookingDAO.getByIntentId(intentId);
            Player player = playerService.getPlayerById(intent.getPlayerId());
            Club club = clubService.getClubById(intent.getClubId());
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubId(intent.getClubId())
                            .stream()
                            .collect(Collectors.toMap(
                                    Station::getId,
                                    Function.identity()));

            return getBookingResponse(booking, intent, player, club, stationMap);
        } catch (BookingIntentDaoException e) {
            log.error("Failed to fetch booking-intent with intentId: {}", intentId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        } catch (DataNotFoundException e) {
            log.error("Booking not found for intentId: {}", intentId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingDAOException e) {
            log.error("Failed to fetch booking with intentId: {}", intentId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        }
    }

    @Override
    public BookingDetailsResponse getClubBookingById(Long clubId, Long bookingId)
            throws BookingServiceException {
        try {
            Booking booking = bookingDAO.getById(bookingId);
            if(!booking.getClubId().equals(clubId)){
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_NOT_FOUND);
            }
            Club club = clubService.getClubById(booking.getClubId());
            Player player = playerService.getPlayerById(booking.getPlayerId());
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubId(booking.getClubId())
                            .stream()
                            .collect(Collectors.toMap(
                                    Station::getId,
                                    Function.identity()));
            BookingIntent bookingIntent =
                    bookingIntentDao.getById(booking.getBookingIntentId());

            return getBookingResponse(booking, bookingIntent, player, club, stationMap);

        } catch (DataNotFoundException e) {
            log.error("Club-Booking not found with ID: {}", bookingId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingIntentDaoException e) {
            log.error("Failed to fetch booking-intent for bookingId: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        } catch (BookingDAOException e) {
            log.error("Failed to fetch booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        }
    }

    @Override
    public BookingDetailsResponse getPlayerBookingById(Long playerId, Long bookingId)
            throws BookingServiceException {
        try {
            Booking booking = bookingDAO.getById(bookingId);
            if(!booking.getPlayerId().equals(playerId)){
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_NOT_FOUND);
            }
            Club club = clubService.getClubById(booking.getClubId());
            Player player = playerService.getPlayerById(booking.getPlayerId());
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubId(booking.getClubId())
                            .stream()
                            .collect(Collectors.toMap(
                                    Station::getId,
                                    Function.identity()));
            BookingIntent bookingIntent =
                    bookingIntentDao.getById(booking.getBookingIntentId());

            return getBookingResponse(booking, bookingIntent, player, club, stationMap);

        } catch (DataNotFoundException e) {
            log.error("Player-Booking not found with ID: {}", bookingId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingIntentDaoException e) {
            log.error("Failed to fetch booking-intent for bookingId: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        } catch (BookingDAOException e) {
            log.error("Failed to fetch booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.GET_BOOKING_FAILED);
        }
    }

    @Override
    @Transactional
    public void cancelClubBooking(Long clubId, Long bookingId)
            throws BookingServiceException {
        try {
            Booking booking = bookingDAO.getById(bookingId);
            BookingIntent bookingIntent =
                    bookingIntentDao.getById(booking.getBookingIntentId());
            if(!booking.getClubId().equals(clubId)
                    || !BookingIntentMode.OFFLINE.equals(bookingIntent.getIntentMode())){
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_NOT_FOUND);
            }
            booking.setStatus(BookingStatus.CANCELLED);
            bookingDAO.update(bookingId, booking);

        } catch (DataNotFoundException e) {
            log.error("Club-Booking not found with ID {} for cancel", bookingId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingIntentDaoException e) {
            log.error("Failed to fetch booking-intent for booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_FAILED);
        } catch (BookingDAOException e) {
            log.error("Failed to cancel booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_FAILED);
        }
    }

    @Override
    @Transactional
    public void cancelPlayerBooking(Long playerId, Long bookingId)
            throws BookingServiceException {
        try {
            Booking booking = bookingDAO.getById(bookingId);
            BookingIntent bookingIntent =
                    bookingIntentDao.getById(booking.getBookingIntentId());
            if(!booking.getPlayerId().equals(playerId)
                    || !BookingIntentMode.ONLINE.equals(bookingIntent.getIntentMode())){
                throw new BookingServiceException(
                        BookingServiceExceptionType.BOOKING_NOT_FOUND);
            }
            booking.setStatus(BookingStatus.CANCELLED);
            bookingDAO.update(bookingId, booking);

        } catch (DataNotFoundException e) {
            log.error("Player-Booking not found with ID {} for cancel", bookingId);
            throw new BookingServiceException(
                    BookingServiceExceptionType.BOOKING_NOT_FOUND);
        } catch (BookingIntentDaoException e) {
            log.error("Failed to fetch booking-intent for booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_FAILED);
        } catch (BookingDAOException e) {
            log.error("Failed to cancel booking with ID: {}", bookingId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.CANCEL_BOOKING_FAILED);
        }
    }

    @Override
    public List<BookingDetailsResponse> listBookingByPlayerId(Long playerId)
            throws BookingServiceException {
        try {
            List<Booking> bookings = bookingDAO.getBookingsByPlayerId(playerId);

            if (bookings.isEmpty()) {
                return List.of();
            }

            Player player = playerService.getPlayerById(playerId);

            List<Long> clubIds = bookings.stream()
                    .map(Booking::getClubId)
                    .distinct()
                    .toList();

            Map<Long, Club> clubMap = clubService.getClubsByIds(clubIds).stream()
                    .collect(Collectors.toMap(Club::getId, Function.identity()));

            List<Long> intentIds = bookings.stream()
                    .map(Booking::getBookingIntentId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<Long, BookingIntent> intentMap = intentIds.isEmpty()
                    ? Collections.emptyMap()
                    : bookingIntentDao.getIntentsByIds(intentIds).stream()
                    .collect(Collectors.toMap(BookingIntent::getId, Function.identity()));

            Map<Long, Station> stationMap = clubService.getStationsByClubIds(clubIds).stream()
                    .collect(Collectors.toMap(Station::getId, Function.identity()));

            return bookings.stream()
                    .map(b -> getBookingResponse(
                            b,
                            intentMap.get(b.getBookingIntentId()),
                            player,
                            clubMap.get(b.getClubId()),
                            stationMap))
                    .toList();

        }  catch (BookingIntentDaoException e) {
            log.error("Failed to list booking-intents for playerId: {}", playerId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.LIST_BOOKINGS_BY_CLUB_FAILED);
        } catch (BookingDAOException e) {
            log.error("Failed to list bookings for playerId: {}", playerId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.LIST_BOOKINGS_BY_PLAYER_FAILED);
        }
    }


    @Override
    public Page<BookingDetailsResponse> listBookingByClubIdWithFilters(
            Long clubId,
            ClubBookingsListFilterRequest filterRequest,
            Pageable pageable
    ) throws BookingServiceException {
        try {
            Set<StationType> stationTypeSet =
                    filterRequest.getStationTypes() != null
                            ? new HashSet<>(filterRequest.getStationTypes())
                            : null;

            Set<BookingStatus> bookingStatusSet =
                    filterRequest.getBookingStatuses() != null
                            ? new HashSet<>(filterRequest.getBookingStatuses())
                            : null;
            Set<BookingType> bookingTypeSet =
                    filterRequest.getBookingTypes() != null
                            ? new HashSet<>(filterRequest.getBookingTypes())
                            : null;

            Page<Booking> bookings =
                    bookingDAO.getBookingsByClubIdWithFilters(
                        clubId,
                        filterRequest.getFromDateTime(),
                        filterRequest.getToDateTime(),
                        stationTypeSet,
                        bookingStatusSet,
                        bookingTypeSet,
                        pageable
                    );

            if (bookings.isEmpty()) {
                return Page.empty(pageable);
            }

            Club club = clubService.getClubById(clubId);
            List<Long> playerIds = bookings.stream()
                    .map(Booking::getPlayerId)
                    .distinct()
                    .toList();

            Map<Long, Player> playerMap = playerService.getPlayersByIds(playerIds).stream()
                    .collect(Collectors.toMap(Player::getId, Function.identity()));

            Map<Long, Station> stationMap = clubService.getStationsByClubId(clubId).stream()
                    .collect(Collectors.toMap(Station::getId, Function.identity()));

            List<Long> intentIds = bookings.stream()
                    .map(Booking::getBookingIntentId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<Long, BookingIntent> intentMap =
                    intentIds.isEmpty()
                            ? Collections.emptyMap()
                            : bookingIntentDao
                            .getIntentsByIds(intentIds)
                            .stream()
                            .collect(
                                    Collectors.toMap(
                                            BookingIntent::getId,
                                            Function.identity()
                                    )
                            );

            return bookings.map(b -> getBookingResponse(
                    b,
                    intentMap.get(b.getBookingIntentId()),
                    playerMap.get(b.getPlayerId()),
                    club,
                    stationMap
            ));

        } catch (BookingIntentDaoException e) {
            log.error("Failed to list booking-intents for clubId: {}", clubId, e);
            throw new BookingServiceException(
                    BookingServiceExceptionType.LIST_BOOKINGS_BY_CLUB_FAILED);
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
            Map<Long, Station> stationMap =
                    clubService.getStationsByClubIdAndType(
                                    request.getClubId(), request.getStationType())
                            .stream()
                            .collect(Collectors.toMap(
                                    Station::getId,
                                    Function.identity()
                            ));

            validateStations(stationMap , request.getStations());

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
                            request.getStations().stream()
                                    .map(BookingStationRequest::getStationId)
                                    .toList(),
                            request.getDurationHrs(),
                            request.getSearchWindow());

            List<LocalDateTime> availableTimes =
                    availableTimeSlots.stream()
                            .map(TimeSlot::getStartTime).toList();

            return BookingAvailabilityByStationResponse.builder()
                    .clubId(request.getClubId())
                    .stationType(request.getStationType())
                    .stations(request.getStations())
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

            if (startTime.isBefore(LocalDateTime.now(Clock.systemUTC()))) {
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


    private void validateStations(
            Map<Long, Station> allowedStations,
            List<BookingStationRequest> requestStations
    ) throws InvalidRequestException {

        if (requestStations == null || requestStations.isEmpty()) {
            throw new InvalidRequestException("At least one station is required for booking");
        }

        for (BookingStationRequest station : requestStations) {
            if (!allowedStations.containsKey(station.getStationId())) {
                log.error("StationId {} not found for club", station.getStationId());
                throw new InvalidRequestException("InvalidStationId: " + station.getStationId());
            }

            if(station.getPlayerCount() < 1
                    || station.getPlayerCount() > allowedStations.get(station.getStationId()).getCapacity()){
                log.error("Invalid playerCount {} for StationId {}",
                        station.getPlayerCount() ,station.getStationId());
                throw new InvalidRequestException("InvalidStationCapacity for stationId {}: " + station.getStationId());
            }
        }
    }

    private void validateStationAvailability(
            Long clubId,
            StationType stationType,
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<BookingStationRequest> requestStations
    ) throws Exception {
        try {
            List<StationAvailability> available =
                    bookingManager.getAvailableStationsForTime(clubId, stationType,
                            startTime, endTime);

            Set<Long> availableIds = available.stream()
                    .filter(StationAvailability::isAvailable)
                    .map(StationAvailability::getStationId)
                    .collect(Collectors.toSet());

            for (BookingStationRequest station : requestStations) {
                if (!availableIds.contains(station.getStationId())) {
                    log.error("StationId {} not available", station.getStationId());
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

    private BookingIntentDetailsResponse getBookingIntentResponse(
            BookingIntent bookingIntent,
            Player player,
            Club club,
            Map<Long, Station> stationMap
    ) {
        return
                BookingIntentDetailsResponse.builder()
                    .intentId(bookingIntent.getId())
                    .club(
                        BookingIntentClubDetails.builder()
                            .clubId(club.getId())
                            .clubName(club.getClubName())
                            .build()
                    )
                    .player(
                        BookingIntentPlayerDetails.builder()
                            .playerId(player.getId())
                            .playerPhoneNumber(player.getPhone())
                            .name(player.getName())
                            .build()
                    )
                    .intent(
                        BookingIntentDetails.builder()
                            .startTime(bookingIntent.getStartTime())
                            .endTime(bookingIntent.getEndTime())
                            .expiresAt(bookingIntent.getExpiresAt())
                            .stationType(bookingIntent.getStationType())
                            .stations(
                                bookingIntent.getStations()
                                    .stream()
                                    .map(station ->
                                            BookingIntentStationDetails.builder()
                                                    .stationId(station.getStationId())
                                                    .stationName(stationMap.get(
                                                            station.getStationId()).getStationName())
                                                    .playerCount(station.getPlayerCount())
                                                    .build()
                                    )
                                    .toList()
                            )
                            .isCancelled(bookingIntent.getIsCancelled())
                            .isConfirmed(bookingIntent.isConfirmed())
                            .costDetails(
                                BookingIntentCostDetails.builder()
                                    .totalCost(bookingIntent.getTotalCost())
                                    .build()
                            )
                        .build()
                    )
                .build();
    }

    private BookingDetailsResponse getBookingResponse(
            Booking booking,
            BookingIntent bookingIntent,
            Player player,
            Club club,
            Map<Long, Station> stationMap
    ) {
        return
            BookingDetailsResponse.builder()
                .bookingId(booking.getId())
                .club(
                    BookingClubDetails.builder()
                        .clubId(club.getId())
                        .clubName(club.getClubName())
                        .build()
                )
                .player(
                    BookingPlayerDetails.builder()
                        .playerId(player.getId())
                        .playerPhoneNumber(player.getPhone())
                        .name(player.getName())
                        .build()
                )
                .booking(
                    BookingDetails.builder()
                        .startTime(booking.getStartTime())
                        .endTime(booking.getEndTime())
                        .stationType(booking.getStationType())
                        .stations(
                            booking.getStations()
                                .stream()
                                .map(station ->
                                    BookingStationDetails.builder()
                                        .stationId(station.getStationId())
                                        .stationName(stationMap.get(
                                                station.getStationId()).getStationName())
                                        .playerCount(station.getPlayerCount())
                                        .build())
                                .toList())
                        .bookingStatus(booking.getStatus())
                        .totalPlayers(bookingIntent.getPlayerCount())
                        .costDetails(
                            BookingCostDetails.builder()
                                .totalCost(bookingIntent.getTotalCost())
                                .build()
                        )
                    .build()
                )
            .build();
    }
}
