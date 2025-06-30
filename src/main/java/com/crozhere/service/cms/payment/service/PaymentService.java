package com.crozhere.service.cms.payment.service;

import com.crozhere.service.cms.payment.controller.model.request.InitPaymentRequest;
import com.crozhere.service.cms.payment.controller.model.request.UpdatePaymentRequest;
import com.crozhere.service.cms.payment.repository.entity.Payment;
import com.crozhere.service.cms.booking.service.exception.PaymentServiceException;

public interface PaymentService {
    Payment initPayment(InitPaymentRequest request) throws PaymentServiceException;
    Payment updatePayment(UpdatePaymentRequest request) throws PaymentServiceException;
    Payment getPaymentById(Long paymentId) throws PaymentServiceException;
}
