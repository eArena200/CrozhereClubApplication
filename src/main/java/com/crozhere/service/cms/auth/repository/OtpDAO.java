package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.repository.exception.OtpDAOException;

public interface OtpDAO {
    void save(OTP otp) throws OtpDAOException;
    OTP get(String token) throws OtpDAOException;
    void update(String token, OTP otp) throws OtpDAOException;
    void delete(String token) throws OtpDAOException;
}
