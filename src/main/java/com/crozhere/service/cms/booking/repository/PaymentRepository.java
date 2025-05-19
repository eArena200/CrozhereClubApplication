package com.crozhere.service.cms.booking.repository;

import com.crozhere.service.cms.booking.repository.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
