package com.crozhere.service.cms.payment.repository.dao.impl;

import com.crozhere.service.cms.payment.repository.PaymentRepository;
import com.crozhere.service.cms.payment.repository.dao.PaymentDao;
import com.crozhere.service.cms.payment.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.payment.repository.dao.exception.PaymentDAOException;
import com.crozhere.service.cms.payment.repository.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentDaoImpl implements PaymentDao {

    private final PaymentRepository paymentRepository;

    @Override
    public void save(Payment payment) throws PaymentDAOException {
        try {
            paymentRepository.save(payment);
        } catch (Exception e){
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
            return paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new DataNotFoundException("GetByIdException"));
        } catch (DataNotFoundException e){
            log.error("Payment not found with ID: {}", paymentId);
            throw e;
        } catch (Exception e){
            log.error("Exception while getting payment for Id: {}", paymentId);
            throw new PaymentDAOException("GetByIdException", e);
        }
    }

    @Override
    public void update(Long paymentId, Payment payment)
            throws DataNotFoundException, PaymentDAOException {
        if(!paymentRepository.existsById(paymentId)){
            log.error("Payment not found with id: {}", paymentId);
            throw new DataNotFoundException("UpdateException");
        }

        payment.setId(paymentId);
        paymentRepository.save(payment);
    }

    @Override
    public void deleteById(Long paymentId) throws PaymentDAOException {
        try {
            paymentRepository.deleteById(paymentId);
        } catch (Exception e){
            log.error("Exception while deleting payment for ID: {}", paymentId);
            throw new PaymentDAOException("DeleteByIdException", e);
        }
    }
}
