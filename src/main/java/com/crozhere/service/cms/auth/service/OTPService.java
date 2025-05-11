package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.service.exception.OTPServiceException;


public interface OTPService {
    String sendOTP(String identifier) throws OTPServiceException;
    Boolean verifyOTP(String token, String otp) throws OTPServiceException;

    String getIdentifierForToken(String token) throws OTPServiceException;
}
