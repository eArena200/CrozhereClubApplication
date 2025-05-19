package com.crozhere.service.cms.booking.service;

import com.crozhere.service.cms.booking.repository.dao.BookingDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingManager {

    private final BookingDao bookingDao;

    @Autowired
    public BookingManager(
            @Qualifier("BookingInMemDao") BookingDao bookingDao){
        this.bookingDao = bookingDao;
    }

}
