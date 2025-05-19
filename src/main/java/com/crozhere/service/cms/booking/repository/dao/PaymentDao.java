package com.crozhere.service.cms.booking.repository.dao;

import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.dao.exception.PaymentDAOException;
import com.crozhere.service.cms.booking.repository.entity.Payment;

import java.util.Optional;

public interface PaymentDao {
    void save(Payment payment) throws PaymentDAOException;

    Optional<Payment> findById(Long paymentId) throws PaymentDAOException;
    Payment getById(Long paymentId) throws DataNotFoundException, PaymentDAOException;

    void update(Long paymentId, Payment payment) throws DataNotFoundException, PaymentDAOException;

    void deleteById(Long paymentId) throws PaymentDAOException;
}

