package com.crozhere.service.cms.payment.repository.dao.exception;


public class PaymentDAOException extends RuntimeException {
    public PaymentDAOException(String message) { super(message); }
    public PaymentDAOException(String message, Throwable cause) { super(message, cause); }
}
