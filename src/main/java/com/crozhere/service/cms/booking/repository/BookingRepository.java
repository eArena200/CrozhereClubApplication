package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
