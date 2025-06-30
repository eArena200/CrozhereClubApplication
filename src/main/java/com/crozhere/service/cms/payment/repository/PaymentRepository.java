package com.crozhere.service.cms.payment.repository;

import com.crozhere.service.cms.payment.repository.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
