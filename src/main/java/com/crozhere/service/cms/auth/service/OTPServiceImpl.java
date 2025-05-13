package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.repository.dao.OtpDao;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.dao.exception.OtpDAOException;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class OTPServiceImpl implements OTPService {

    private final OtpDao otpDao;

    private static final int OTP_EXPIRY_MINUTES = 5;

    @Autowired
    public OTPServiceImpl(
            @Qualifier("OtpSqlDao") OtpDao otpDao){
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
    public Boolean verifyOTP(String phone, String otp)
            throws OTPServiceException {
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
        } catch (DataNotFoundException dataNotFoundException){
            log.info("No otp found for phone: {}", phone);
            throw new OTPServiceException("VerifyOTPException");
        } catch (OtpDAOException otpdaoException){
            log.error("Exception while getting otp for phone: {}", phone);
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
                    .id(UUID.randomUUID().toString())
                    .phone(phone)
                    .used(false)
                    .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                    .otp(generateOtpCode())
                    .build();

            otpDao.save(newOtp);
            return newOtp;
        }
    }

    private String generateOtpCode(){
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }


    private void sendOtpToUser(String phone, String otp) {
        // TODO: Integrate SMS/Email provider
        log.info("Sending OTP {} to phone {}", otp, phone);
    }

}
