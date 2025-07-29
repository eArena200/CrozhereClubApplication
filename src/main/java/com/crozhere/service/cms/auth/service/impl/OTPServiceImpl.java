package com.crozhere.service.cms.auth.service.impl;

import com.crozhere.service.cms.auth.repository.dao.OtpDao;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.dao.exception.OtpDAOException;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.service.OTPService;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class OTPServiceImpl implements OTPService {

    private final OtpDao otpDao;
    private final SnsClient snsClient;

    private static final String IND_PREFIX = "+91";
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Autowired
    public OTPServiceImpl(OtpDao otpDao, SnsClient snsClient) {
        this.otpDao = otpDao;
        this.snsClient = snsClient;
    }

    @Override
    public void sendOTP(String phone) throws OTPServiceException {
        try {
            OTP otp = getOrCreateOtp(phone);
            sendOtpToUser(phone, otp.getOtp());
        } catch (OtpDAOException e) {
            log.error("Failed to send OTP for phone: {}", phone, e);
            throw new OTPServiceException("SendOtpException", e);
        }
    }

    @Override
    public Boolean verifyOTP(String phone, String otp) throws OTPServiceException {
        try {
            OTP otpData = otpDao.getByPhone(phone);

            if (otpData.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new OTPServiceException("OTPExpiredException");
            }

            if (otpData.isUsed()) {
                throw new OTPServiceException("OTPAlreadyUsedException");
            }

            boolean match = otpData.getOtp().equals(otp);

            if (match) {
                otpData.setUsed(true);
                otpDao.updateByPhone(phone, otpData);
            }

            return match;
        } catch (DataNotFoundException e) {
            log.info("No OTP found for phone: {}", phone);
            throw new OTPServiceException("VerifyOTPException");
        } catch (OtpDAOException e) {
            log.error("Exception while getting OTP for phone: {}", phone, e);
            throw new OTPServiceException("VerifyOTPException");
        }
    }

    private OTP getOrCreateOtp(String phone) throws OtpDAOException {
        try {
            OTP otp = otpDao.getByPhone(phone);

            if (!otp.isUsed() && otp.getExpiresAt().isAfter(LocalDateTime.now())) {
                log.info("Reusing active OTP for phone {}: {}", phone, otp.getOtp());
                return otp;
            }

            log.info("Regenerating OTP for phone: {}", phone);
            otp.setOtp(generateOtpCode());
            otp.setUsed(false);
            otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            otpDao.updateByPhone(phone, otp);
            return otp;

        } catch (DataNotFoundException e) {
            log.info("No OTP found for phone {}, creating new", phone);
            OTP newOtp = OTP.builder()
                    .phone(phone)
                    .used(false)
                    .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                    .otp(generateOtpCode())
                    .build();

            otpDao.save(newOtp);
            return newOtp;
        }
    }

    private String generateOtpCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    private void sendOtpToUser(String phone, String otp) {
        String message = "Your Crozhere OTP is: " + otp;
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(IND_PREFIX + phone)
                    .build();

            PublishResponse result = snsClient.publish(request);
            log.info("Sent OTP {} to {} via SNS (MessageId: {})", otp, phone, result.messageId());
        } catch (SnsException e) {
            log.error("Failed to send OTP via SNS to {}: {}", phone, e.awsErrorDetails().errorMessage());
            throw new OTPServiceException("OtpSendFailure", e);
        }
    }
}
