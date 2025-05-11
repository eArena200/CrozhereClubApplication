package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.controller.model.request.CreateBookingRequest;
import com.crozhere.service.cms.booking.repository.BookingDAO;
import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import com.crozhere.service.cms.booking.repository.exception.BookingDAOException;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingDAO bookingDAO;

    public BookingServiceImpl(
            @Qualifier("BookingInMemDAO") BookingDAO bookingDAO){
        this.bookingDAO = bookingDAO;
    }

    @Override
    public Booking createBooking(CreateBookingRequest createBookingRequest)
            throws BookingServiceException {
        Booking booking = Booking.builder().build();
        try {
            bookingDAO.save(booking);
            return booking;
        } catch (BookingDAOException bookingDAOException){
            log.error("Exception while saving booking {} ", booking.getBookingId());
            throw new BookingServiceException("CreateBookingException");
        }
    }

    @Override
    public Booking getBookingById(String bookingId) throws BookingServiceException {
        try {
            return bookingDAO.get(bookingId);
        } catch (BookingDAOException bookingDAOException){
            log.error("Exception while getting booking {} ", bookingId);
            throw new BookingServiceException("GetBookingByIdException");
        }
    }

    @Override
    public Booking cancelBooking(String bookingId) throws BookingServiceException {
        try{
            Booking booking = bookingDAO.get(bookingId);
            booking.setStatus(BookingStatus.CANCELLED);
            bookingDAO.update(bookingId, booking);
            return booking;
        } catch (BookingDAOException bookingDAOException){
            log.error("Exception in cancelBooking: {}", bookingDAOException.getMessage());
            throw new BookingServiceException("CancelBookingException");
        }
    }

    @Override
    public void deleteBooking(String bookingId) throws BookingServiceException {
        try {
            bookingDAO.delete(bookingId);
        } catch (BookingDAOException bookingDAOException){
            log.error("Exception in deleting booking: {}", bookingId);
            throw new BookingServiceException("DeleteBookingException");
        }
    }

    @Override
    public List<Booking> listBookingByPlayerId(String playerId) throws BookingServiceException {
        try {
            return bookingDAO.getBookingByPlayerId(playerId);
        } catch (BookingDAOException bookingDAOException){
            log.error("Exception in getting bookings for playerId: {}", playerId);
            throw new BookingServiceException("GetBookingByPlayerIdException");
        }
    }

    @Override
    public List<Booking> listBookingByClubAdminId(String clubAdminId) throws BookingServiceException {
        try {
            return bookingDAO.getBookingByClubAdminId(clubAdminId);
        } catch (BookingDAOException bookingDAOException){
            log.error("Exception in getting bookings for clubAdminId: {}", clubAdminId);
            throw new BookingServiceException("GetBookingByPlayerIdException");
        }
    }
}
