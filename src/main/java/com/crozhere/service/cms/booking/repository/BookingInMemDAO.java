package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.exception.BookingDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component("InMem")
public class BookingInMemDAO implements BookingDAO{

    private final Map<String, Booking> bookingStore;

    public BookingInMemDAO(){
        this.bookingStore = new HashMap<>();
    }


    @Override
    public void save(Booking booking) throws BookingDAOException {
        if(bookingStore.containsKey(booking.getBookingId())){
            throw new BookingDAOException("BookingId already exists");
        }

        bookingStore.putIfAbsent(booking.getBookingId(), booking);
    }

    @Override
    public Booking get(String bookingId) throws BookingDAOException {
        if(bookingStore.containsKey(bookingId)){
            return bookingStore.get(bookingId);
        }

        throw new BookingDAOException("BookingId doesn't exist");
    }

    @Override
    public void update(String bookingId, Booking booking) throws BookingDAOException {
        if(bookingStore.containsKey(bookingId)){
            bookingStore.put(bookingId, booking);
        } else {
            throw new BookingDAOException("BookingId doesn't exist");
        }
    }

    @Override
    public void delete(String bookingId) throws BookingDAOException {
        if(bookingStore.containsKey(bookingId)){
            bookingStore.remove(bookingId);
        } else {
            throw new BookingDAOException("BookingId doesn't exist");
        }
    }

    @Override
    public List<Booking> getBookingByPlayerId(String playerId) throws BookingDAOException {
        try {
            return bookingStore.values().stream()
                    .filter(booking -> booking.getPlayerId().equals(playerId))
                    .collect(Collectors.toList());
        } catch (Exception e){
            log.error("Exception in getting bookings for playerId: {}", playerId);
            throw new BookingDAOException("Exception in GetBookingByPlayerId", e);
        }
    }

    // TODO: Complete it.
    @Override
    public List<Booking> getBookingByClubAdminId(String clubAdminId) throws BookingDAOException {
        try {
            return List.of();
        } catch (Exception e){
            log.error("Exception in getting bookings for clubAdminId: {}", clubAdminId);
            throw new BookingDAOException("Exception in GetBookingByClubAdminId", e);
        }
    }
}
