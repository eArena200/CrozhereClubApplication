package com.crozhere.service.cms.payment.service.impl;

import com.crozhere.service.cms.booking.controller.model.request.ConfirmBookingIntentRequest;
import com.crozhere.service.cms.payment.controller.model.request.InitPaymentRequest;
import com.crozhere.service.cms.payment.controller.model.request.UpdatePaymentRequest;
import com.crozhere.service.cms.payment.repository.dao.PaymentDao;
import com.crozhere.service.cms.booking.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.payment.repository.dao.exception.PaymentDAOException;
import com.crozhere.service.cms.booking.repository.entity.BookingIntent;
import com.crozhere.service.cms.payment.repository.entity.Payment;
import com.crozhere.service.cms.payment.repository.entity.PaymentMode;
import com.crozhere.service.cms.payment.repository.entity.PaymentStatus;
import com.crozhere.service.cms.booking.service.BookingService;
import com.crozhere.service.cms.payment.service.PaymentService;
import com.crozhere.service.cms.booking.service.exception.BookingServiceException;
import com.crozhere.service.cms.booking.service.exception.BookingServiceExceptionType;
import com.crozhere.service.cms.booking.service.exception.PaymentServiceException;
import com.crozhere.service.cms.booking.service.exception.PaymentServiceExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;
    private final BookingService bookingService;

    @Override
    @Transactional
    public Payment initPayment(InitPaymentRequest request)
            throws PaymentServiceException {
        try {
            BookingIntent bookingIntent =
                    bookingService.getBookingIntentById(request.getIntentId());

            if(PaymentMode.CASH.equals(request.getPaymentMode())){
                Payment payment = Payment.builder()
                        .intentId(request.getIntentId())
                        .paymentMode(request.getPaymentMode())
                        .status(PaymentStatus.SUCCESS)
                        .amount(request.getAmount())
                        .build();

                paymentDao.save(payment);

                ConfirmBookingIntentRequest confirmBookingIntentRequest =
                        ConfirmBookingIntentRequest.builder()
                                .bookingIntentId(bookingIntent.getId())
                                .paymentId(payment.getId())
                                .build();
                bookingService.confirmBookingIntent(confirmBookingIntentRequest);

                return payment;
            } else {
                log.error("Payment mode {} not supported", request.getPaymentMode());
                throw new PaymentServiceException(PaymentServiceExceptionType.UNSUPPORTED_PAYMENT_MODE);
            }

        } catch (BookingServiceException e) {
            if(e.getType().equals(BookingServiceExceptionType.BOOKING_INTENT_NOT_FOUND)){
                log.error("Intent with Id {} not found for payment", request.getIntentId());
                throw new PaymentServiceException(PaymentServiceExceptionType.INVALID_REQUEST);
            }

            if(e.getType().equals(BookingServiceExceptionType.CONFIRM_BOOKING_INTENT_FAILED)){
                log.error("Failed to confirm booking for intent with Id: {}", request.getIntentId());
                throw new PaymentServiceException(PaymentServiceExceptionType.PAYMENT_CREATION_FAILED);
            }

            log.error("Unknown exception in Booking service", e);
            throw new PaymentServiceException(PaymentServiceExceptionType.PAYMENT_CREATION_FAILED);
        } catch (PaymentDAOException e) {
            log.error("Exception while saving payment", e);
            throw new PaymentServiceException(PaymentServiceExceptionType.PAYMENT_CREATION_FAILED);
        }
    }

    @Override
    public Payment updatePayment(UpdatePaymentRequest request)
            throws PaymentServiceException {
        // TODO: Implement it for the webhook using factory for different payment processor based on provider
        return null;
    }

    @Override
    public Payment getPaymentById(Long paymentId) throws PaymentServiceException {
        try {
            return paymentDao.getById(paymentId);
        } catch (DataNotFoundException e){
            log.error("Payment not found with paymentId: {}", paymentId);
            throw new PaymentServiceException(PaymentServiceExceptionType.PAYMENT_NOT_FOUND);
        } catch (PaymentDAOException e){
            log.error("Exception while getting payment with paymentId: {}", paymentId);
            throw new PaymentServiceException(PaymentServiceExceptionType.GET_PAYMENT_FAILED);
        }
    }
}
