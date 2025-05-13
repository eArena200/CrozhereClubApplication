package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.dao.OtpDao;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.dao.exception.OtpDAOException;
import com.crozhere.service.cms.auth.repository.entity.OTP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("OtpInMemDao")
public class OtpInMemDao implements OtpDao {

    private final Map<String, OTP> storeById = new HashMap<>();
    private final Map<String, OTP> storeByPhone = new HashMap<>();

    @Override
    public void save(OTP otp) throws OtpDAOException {
        if (storeByPhone.containsKey(otp.getPhone())
                || storeById.containsKey(otp.getId())) {
            throw new OtpDAOException("OTP already exists for ID or phone");
        }
        storeById.put(otp.getId(), otp);
        storeByPhone.put(otp.getPhone(), otp);
    }

    @Override
    public OTP getById(String id) throws DataNotFoundException, OtpDAOException {
        if(storeById.containsKey(id)) {
            return storeById.get(id);
        } else {
            throw new DataNotFoundException();
        }
    }

    @Override
    public OTP getByPhone(String phone) throws DataNotFoundException, OtpDAOException {
        if(storeByPhone.containsKey(phone)) {
            return storeByPhone.get(phone);
        } else {
            throw new DataNotFoundException();
        }
    }

    @Override
    public void updateById(String id, OTP updatedOtp) throws DataNotFoundException, OtpDAOException {
        OTP existing = getById(id);
        storeById.put(id, updatedOtp);
        storeByPhone.put(updatedOtp.getPhone(), updatedOtp);
    }

    @Override
    public void updateByPhone(String phone, OTP updatedOtp)
            throws DataNotFoundException, OtpDAOException {
        OTP existing = getByPhone(phone);
        storeByPhone.put(phone, updatedOtp);
        storeById.put(updatedOtp.getId(), updatedOtp);
    }

    @Override
    public void deleteById(String id) throws DataNotFoundException, OtpDAOException {
        if(storeById.containsKey(id)) {
            storeById.remove(id);
        } else {
            throw new DataNotFoundException();
        }
    }

    @Override
    public void deleteByPhone(String phone) throws DataNotFoundException, OtpDAOException {
        if(storeByPhone.containsKey(phone)) {
            storeByPhone.remove(phone);
        } else {
            throw new DataNotFoundException();
        }
    }
}
