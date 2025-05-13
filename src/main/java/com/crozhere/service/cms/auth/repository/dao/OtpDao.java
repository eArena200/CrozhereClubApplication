package com.crozhere.service.cms.auth.repository.dao;

import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.repository.dao.exception.OtpDAOException;

public interface OtpDao {
    void save(OTP otp) throws OtpDAOException;

    OTP getById(Long id) throws DataNotFoundException, OtpDAOException;
    OTP getByPhone(String phone) throws DataNotFoundException, OtpDAOException;

    void updateById(Long id, OTP otp) throws DataNotFoundException, OtpDAOException;
    void updateByPhone(String phone, OTP otp) throws DataNotFoundException, OtpDAOException;

    void deleteById(Long id) throws DataNotFoundException, OtpDAOException;
    void deleteByPhone(String phone) throws DataNotFoundException, OtpDAOException;
}
