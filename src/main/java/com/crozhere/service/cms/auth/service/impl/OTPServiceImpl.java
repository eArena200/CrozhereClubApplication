package com.crozhere.service.cms.auth.service.impl;

import com.crozhere.service.cms.auth.repository.dao.OtpDao;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.dao.exception.OtpDAOException;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.service.OTPService;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class OTPServiceImpl implements OTPService {

    private final OtpDao otpDao;

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    private static final String IND_PREFIX = "+91";
    private static final int OTP_EXPIRY_MINUTES = 5;

    public OTPServiceImpl(OtpDao otpDao) {
        this.otpDao = otpDao;
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
        String toPhoneNumber = IND_PREFIX + phone;
        String message = "Your Crozhere OTP is: " + otp;

        try {
            Twilio.init(accountSid, authToken);

            Message twilioMessage = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message
            ).create();

            log.info("Sent OTP {} to {} via Twilio (SID: {})", otp, toPhoneNumber, twilioMessage.getSid());

        } catch (Exception e) {
            log.error("Failed to send OTP via Twilio to {}: {}", toPhoneNumber, e.getMessage());
            throw new OTPServiceException("OtpSendFailure", e);
        }
    }
}
