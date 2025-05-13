package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.service.exception.OTPServiceException;


public interface OTPService {
    void sendOTP(String phone) throws OTPServiceException;
    Boolean verifyOTP(String phone, String otp) throws OTPServiceException;
}
