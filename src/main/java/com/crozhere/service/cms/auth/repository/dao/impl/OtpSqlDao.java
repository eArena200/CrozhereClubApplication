package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.OtpRepository;
import com.crozhere.service.cms.auth.repository.dao.OtpDao;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.dao.exception.OtpDAOException;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("OtpSqlDao")
public class OtpSqlDao implements OtpDao {

    private final OtpRepository otpRepository;

    @Autowired
    public OtpSqlDao(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public void save(OTP otp) throws OtpDAOException {
        try {
            otpRepository.save(otp);
        } catch (Exception e) {
            log.error("Failed to save OTP: {}", otp, e);
            throw new OtpDAOException("SaveException");
        }
    }

    @Override
    public OTP getById(String id) throws DataNotFoundException, OtpDAOException {
        try {
            return otpRepository.findById(id)
                    .orElseThrow(DataNotFoundException::new);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Database error while fetching OTP by ID: {}", id, e);
            throw new OtpDAOException("GetByIdException");
        }
    }

    @Override
    public OTP getByPhone(String phone) throws DataNotFoundException, OtpDAOException {
        try {
            return otpRepository.findByPhone(phone)
                    .orElseThrow(DataNotFoundException::new);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Database error while fetching OTP by phone: {}", phone, e);
            throw new OtpDAOException("GetByPhoneException");
        }
    }

    @Override
    public void updateById(String id, OTP otp) throws OtpDAOException {
        try {
            OTP existing = getById(id);
            updateFields(existing, otp);
            otpRepository.save(existing);
        } catch (Exception e) {
            log.error("Failed to update OTP by ID: {}", id, e);
            throw new OtpDAOException("UpdateByIdException");
        }
    }

    @Override
    public void updateByPhone(String phone, OTP otp) throws OtpDAOException {
        try {
            OTP existing = getByPhone(phone);
            updateFields(existing, otp);
            otpRepository.save(existing);
        } catch (Exception e) {
            log.error("Failed to update OTP by phone: {}", phone, e);
            throw new OtpDAOException("UpdateByPhoneException");
        }
    }

    @Override
    public void deleteById(String id) throws OtpDAOException {
        try {
            otpRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete OTP by ID: {}", id, e);
            throw new OtpDAOException("DeleteByIdException", e);
        }
    }

    @Override
    public void deleteByPhone(String phone) throws OtpDAOException {
        try {
            Optional<OTP> otp = otpRepository.findByPhone(phone);
            otp.ifPresent(otpRepository::delete);
        } catch (Exception e) {
            log.error("Failed to delete OTP by phone: {}", phone, e);
            throw new OtpDAOException("DeleteByPhoneException", e);
        }
    }

    private void updateFields(OTP target, OTP source) {
        target.setOtp(source.getOtp());
        target.setUsed(source.isUsed());
        target.setExpiresAt(source.getExpiresAt());
    }
}
