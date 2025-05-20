package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.controller.model.request.SearchWindow;
import com.crozhere.service.cms.booking.controller.model.response.StationAvailability;
import com.crozhere.service.cms.booking.service.exception.BookingManagerException;
import com.crozhere.service.cms.booking.service.exception.InvalidRequestException;
import com.crozhere.service.cms.booking.util.TimeSlot;
import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import com.crozhere.service.cms.booking.repository.dao.exception.BookingDAOException;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.util.TimeSlotUtil;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BookingManager {

    private final BookingDao bookingDao;
    private final ClubService clubService;

    @Autowired
    public BookingManager(
            @Qualifier("BookingInMemDao") BookingDao bookingDao,
            ClubService clubService){
        this.bookingDao = bookingDao;
        this.clubService = clubService;
    }

    public List<StationAvailability> getAvailableStationsForTime(
            Long clubId, StationType stationType,
            LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        try {
            List<Station> stations = clubService.getStationsByClubIdAndType(clubId, stationType);
            List<Booking> overlappingBookings =
                    bookingDao.getOverlappingBookings(stations, startTime, endTime);

            Set<Long> bookedStationIds = overlappingBookings.stream()
                    .flatMap(b -> b.getStations().stream())
                    .map(Station::getId)
                    .collect(Collectors.toSet());

            return stations.stream()
                    .map(station -> StationAvailability.builder()
                            .stationId(station.getId())
                            .stationName(station.getStationName())
                            .stationType(station.getStationType())
                            .isAvailable(!bookedStationIds.contains(station.getId()))
                            .build())
                    .toList();
        } catch (ClubServiceException e) {
            log.error("Exception in getting stations for availability");
            throw e;
        } catch (BookingDAOException e) {
            log.error("Exception in getting bookings for stations");
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while checking availability by time");
            throw e;
        }
    }

    public List<TimeSlot> getAvailableTimeSlotsForStations(
            Long clubId, StationType stationType,
            List<Long> stationIds, Integer durationInHrs,
            SearchWindow searchWindow) throws Exception {
        try {
            List<Station> stations =
                    clubService.getStationsByClubIdAndType(clubId, stationType)
                            .stream()
                            .filter(station -> stationIds.contains(station.getId()))
                            .toList();

            List<Booking> bookings = bookingDao.getBookingsForStations(stations);
            Map<Long, List<TimeSlot>> busySlots = new HashMap<>();
            for (Station station : stations) {
                busySlots.put(station.getId(),
                        bookings.stream()
                                .filter(b -> b.getStations().contains(station))
                                .map(b -> TimeSlot.builder()
                                        .startTime(b.getStartTime())
                                        .endTime(b.getEndTime())
                                        .build())
                                .toList());
            }

            return findCommonAvailableSlots(
                            busySlots, durationInHrs, searchWindow);

        } catch (ClubServiceException e) {
            log.error("Exception in getting time-slots for availability");
            throw e;
        } catch (BookingDAOException e) {
            log.error("Exception in getting bookings for stations");
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while checking availability by stations");
            throw e;
        }
    }

    private List<TimeSlot> findCommonAvailableSlots(
            Map<Long, List<TimeSlot>> busySlotsPerStation,
            Integer durationInHours,
            SearchWindow searchWindow) {
        LocalDateTime windowStart = searchWindow.getDateTime();
        LocalDateTime windowEnd = windowStart.plusHours(searchWindow.getWindowHrs() + durationInHours);

        List<List<TimeSlot>> allFree = busySlotsPerStation.values().stream()
                .map(busy -> {
                    List<TimeSlot> merged = TimeSlotUtil.mergeIntervals(busy);
                    return TimeSlotUtil.invert(merged, windowStart, windowEnd);
                })
                .toList();

        return TimeSlotUtil.intersectFreeSlots(allFree, durationInHours, searchWindow);
    }

}
