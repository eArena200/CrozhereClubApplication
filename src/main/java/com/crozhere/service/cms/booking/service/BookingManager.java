package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.SearchWindow;
import com.crozhere.service.cms.booking.controller.model.response.StationAvailability;
import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.repository.dao.BookingIntentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingIntentDaoException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.booking.repository.entity.BookingIntentStation;
import com.crozhere.service.cms.booking.repository.entity.BookingStation;
import com.crozhere.service.cms.booking.service.exception.BookingManagerException;
import com.crozhere.service.cms.booking.util.TimeSlot;
import com.crozhere.service.cms.booking.util.TimeSlotUtil;
import com.crozhere.service.cms.club.controller.model.response.StationResponse;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingManager {

    private final BookingDao bookingDao;
    private final BookingIntentDao bookingIntentDao;
    private final ClubService clubService;

    public List<StationAvailability> getAvailableStationsForTime(
            Long clubId,
            StationType stationType,
            Instant startTime,
            Instant endTime
    ) throws BookingManagerException {
        try {
            List<StationResponse> stations = clubService.getStationsByClubIdAndType(clubId, stationType);
            log.info("FOUND STATIONS: {}", stations);

            List<Booking> bookings =
                    bookingDao.getBookingsForStationsAndForSearchWindow(stations, startTime, endTime);
            log.info("FOUND OVERLAPPING BOOKINGS: {}", bookings);

            List<BookingIntent> activeIntents =
                    bookingIntentDao.getActiveIntentsForStationsAndForSearchWindow(stations, startTime, endTime);
            log.info("FOUND ACTIVE INTENTS: {}", activeIntents);

            Set<Long> bookedStationIds = bookings.stream()
                    .flatMap(b -> b.getStations().stream())
                    .map(BookingStation::getStationId)
                    .collect(Collectors.toSet());

            Set<Long> intentStationIds = activeIntents.stream()
                    .flatMap(intent -> intent.getStations().stream())
                    .map(BookingIntentStation::getStationId)
                    .collect(Collectors.toSet());

            Set<Long> allUnavailable = new HashSet<>();
            allUnavailable.addAll(bookedStationIds);
            allUnavailable.addAll(intentStationIds);

            return stations.stream()
                    .map(station -> StationAvailability.builder()
                            .stationId(station.getStationId())
                            .isAvailable(!allUnavailable.contains(station.getStationId()))
                            .build())
                    .toList();

        } catch (ClubServiceException e) {
            log.error("Exception in ClubService while checking station availability", e);
            throw new BookingManagerException("Availability check failed");
        } catch (BookingDAOException e) {
            log.error("Exception in BookingDao while getting bookings for getAvailableStationsForTime", e);
            throw new BookingManagerException("Availability check failed");
        } catch (BookingIntentDaoException e) {
            log.error("Exception in BookingIntentDao while getting intents for getAvailableStationsForTime", e);
            throw new BookingManagerException("Availability check failed");
        } catch (Exception e) {
            log.error("Unexpected error in getAvailableStationsForTime", e);
            throw new BookingManagerException("Availability check failed");
        }
    }

    public List<TimeSlot> getAvailableTimeSlotsForStations(
            Long clubId,
            StationType stationType,
            List<Long> stationIds,
            Integer durationInHrs,
            SearchWindow searchWindow
    ) throws BookingManagerException {
        try {
            List<StationResponse> stations = clubService.getStationsByClubIdAndType(clubId, stationType)
                    .stream()
                    .filter(s -> stationIds.contains(s.getStationId()))
                    .toList();
            log.info("FILTERED STATIONS: {}", stations);

            Instant windowStart = searchWindow.getDateTime();
            Instant windowEnd = windowStart.plus(searchWindow.getWindowHrs() + durationInHrs, ChronoUnit.HOURS);

            List<Booking> bookings =
                    bookingDao.getBookingsForStationsAndForSearchWindow(stations, windowStart, windowEnd);
            List<BookingIntent> intents =
                    bookingIntentDao.getActiveIntentsForStationsAndForSearchWindow(stations, windowStart, windowEnd);

            log.info("BOOKINGS: {}", bookings);
            log.info("INTENTS: {}", intents);

            Map<Long, List<TimeSlot>> busySlots = new HashMap<>();

            for (StationResponse station : stations) {
                List<TimeSlot> bookingBusySlots = bookings.stream()
                        .filter(b -> b.getStations().stream()
                                .anyMatch(bs -> bs.getStationId().equals(station.getStationId())))
                        .map(b -> TimeSlot.builder()
                                .startTime(b.getStartTime())
                                .endTime(b.getEndTime())
                                .build())
                        .toList();


                List<TimeSlot> intentBusySlots = intents.stream()
                        .filter(i -> i.getStations().stream()
                                .anyMatch(is -> is.getStationId().equals(station.getStationId())))
                        .map(i -> TimeSlot.builder()
                                .startTime(i.getStartTime())
                                .endTime(i.getEndTime())
                                .build())
                        .toList();

                List<TimeSlot> totalBusy = new ArrayList<>();
                totalBusy.addAll(bookingBusySlots);
                totalBusy.addAll(intentBusySlots);

                busySlots.put(station.getStationId(), totalBusy);
            }

            log.info("BUSY SLOTS PER STATION: {}", busySlots);

            List<TimeSlot> freeSlots = findCommonAvailableSlots(busySlots, durationInHrs, searchWindow);
            log.info("AVAILABLE TIME SLOTS: {}", freeSlots);

            return freeSlots;

        } catch (ClubServiceException e) {
            log.error("Exception in ClubService for getAvailableTimeSlotsForStations", e);
            throw new BookingManagerException("Availability check failed");
        } catch (BookingDAOException e) {
            log.error("Exception in BookingDao while getting bookings for getAvailableTimeSlotsForStations", e);
            throw new BookingManagerException("Availability check failed");
        } catch (BookingIntentDaoException e) {
            log.error("Exception in BookingIntentDao while getting intents for getAvailableTimeSlotsForStations", e);
            throw new BookingManagerException("Availability check failed");
        } catch (Exception e) {
            log.error("Unexpected error in getAvailableTimeSlotsForStations", e);
            throw new BookingManagerException("Availability check failed");
        }
    }

    private List<TimeSlot> findCommonAvailableSlots(
            Map<Long, List<TimeSlot>> busySlotsPerStation,
            Integer durationInHours,
            SearchWindow searchWindow
    ) {
        Instant windowStart = searchWindow.getDateTime();
        Instant windowEnd =
                windowStart.plus(searchWindow.getWindowHrs() + durationInHours,
                        ChronoUnit.HOURS);

        List<List<TimeSlot>> allFree = busySlotsPerStation.values().stream()
                .map(busy -> {
                    List<TimeSlot> merged = TimeSlotUtil.mergeIntervals(busy);
                    return TimeSlotUtil.invert(merged, windowStart, windowEnd);
                })
                .toList();

        return TimeSlotUtil.intersectFreeSlots(allFree, durationInHours, searchWindow);
    }
}
