package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.repository.OtpDAO;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.repository.entity.OTPChannel;
import com.crozhere.service.cms.auth.repository.exception.OtpDAOException;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class OTPServiceImpl implements OTPService {

    private final OtpDAO otpDao;

    @Autowired
    public OTPServiceImpl(OtpDAO otpDao){
        this.otpDao = otpDao;
    }

    @Override
    public String sendOTP(String identifier) throws OTPServiceException {
        try {
            String otp = String.valueOf(new Random().nextInt(900000) + 100000);
            OTP otpData = OTP.builder()
                    .token(UUID.randomUUID().toString())
                    .identifier(identifier)
                    .otpChannel(getOTPChannel(identifier))
                    .otp(otp)
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .build();

            otpDao.save(otpData);
            log.info("Generated OTP for identifier {} : {}", identifier, otp);
            // TODO: Add sending logic

            return otpData.getToken();
        } catch (OtpDAOException otpdaoException){
            log.error("Exception while saving otp for identifier: {}", identifier);
            throw new OTPServiceException("SendOTPException");
        }
    }

    @Override
    public Boolean verifyOTP(String token, String otp)
            throws OTPServiceException {
        try {
            OTP otpData = otpDao.get(token);


            if (otpData.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new OTPServiceException("OTPExpired");
            }

            return otpData.getOtp().equals(otp);

        } catch (OtpDAOException otpdaoException){
            log.error("Exception while getting otp for token: {}", token);
            throw new OTPServiceException("VerifyOTPException");
        }
    }

    @Override
    public String getIdentifierForToken(String token) throws OTPServiceException {
        try {
            OTP otpData = otpDao.get(token);
            return otpData.getIdentifier();
        } catch (OtpDAOException otpDAOException){
            log.error("Exception while getting Identifier for token: {}", token);
            throw new OTPServiceException("GetIdentifierException");
        }
    }

    private OTPChannel getOTPChannel(String identifer) {
        return OTPChannel.PHONE;
    }
}
