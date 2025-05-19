package com.crozhere.service.cms.booking.repository.dao.impl;

import com.crozhere.service.cms.booking.repository.dao.PaymentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.booking.repository.dao.exception.PaymentDAOException;
import com.crozhere.service.cms.booking.repository.entity.Payment;
import com.crozhere.service.cms.common.IdSetters;
import com.crozhere.service.cms.common.InMemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component("PaymentInMemDao")
public class PaymentInMemDao implements PaymentDao {

    private final InMemRepository<Payment> paymentRepository
            = new InMemRepository<>(IdSetters.PAYMENT_ID_SETTER);

    @Override
    public void save(Payment payment) throws PaymentDAOException {
        try {
            paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Failed to save payment: {}", payment.toString());
            throw new PaymentDAOException("SaveException", e);
        }
    }

    @Override
    public Optional<Payment> findById(Long paymentId) throws PaymentDAOException {
        try {
            return paymentRepository.findById(paymentId);
        } catch (Exception e) {
            log.error("Exception in findById for paymentId: {}", paymentId);
            throw new PaymentDAOException("FindByIdException", e);
        }
    }

    @Override
    public Payment getById(Long paymentId) throws DataNotFoundException, PaymentDAOException {
        try {
            return paymentRepository.getById(paymentId);
        } catch (NoSuchElementException e){
            log.error("Payment not found with Id: {}", paymentId);
            throw new DataNotFoundException("GetByIdException");
        } catch (Exception e){
            log.error("Exception while getting payment for Id: {}", paymentId);
            throw new PaymentDAOException("GetByIdException", e);
        }
    }

    @Override
    public void update(Long paymentId, Payment payment)
            throws DataNotFoundException, PaymentDAOException {
        try {
            paymentRepository.update(paymentId, payment);
        } catch (NoSuchElementException e){
            log.error("Payment not found for update with Id: {}", paymentId);
            throw new DataNotFoundException("UpdateException");
        } catch (Exception e){
            log.error("Exception while updating payment for Id: {}", paymentId);
            throw new PaymentDAOException("UpdateException", e);
        }
    }

    @Override
    public void deleteById(Long paymentId) throws PaymentDAOException {
        try {
            paymentRepository.deleteById(paymentId);
        } catch (NoSuchElementException e){
            log.error("Payment not found for delete with Id: {}", paymentId);
            throw new DataNotFoundException("DeleteByIdException");
        } catch (Exception e){
            log.error("Exception while deleting payment for Id: {}", paymentId);
            throw new PaymentDAOException("DeleteByIdException", e);
        }
    }
}

