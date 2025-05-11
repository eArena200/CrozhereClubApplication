package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.OTP;
import com.crozhere.service.cms.auth.repository.exception.OtpDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("OtpInMemDAO")
public class OtpInMemDAO implements OtpDAO {

    private final Map<String, OTP> otpStore;

    public OtpInMemDAO(){
        this.otpStore = new HashMap<>();
    }

    @Override
    public void save(OTP otp) throws OtpDAOException {
         if(otpStore.containsKey(otp.getToken())){
             log.info("token {} already exists", otp.getToken());
             throw new OtpDAOException("SaveException");
         }

         otpStore.putIfAbsent(otp.getToken(), otp);
    }

    @Override
    public OTP get(String token) throws OtpDAOException {
        if(otpStore.containsKey(token)){
            return otpStore.get(token);
        } else {
            log.info("token {} doesn't exist", token);
            throw new OtpDAOException("ReadException");
        }
    }

    @Override
    public void update(String token, OTP otp) throws OtpDAOException {
        if(otpStore.containsKey(token)){
            otpStore.put(token, otp);
        } else {
            log.info("token {} doesn't exist for update", token);
            throw new OtpDAOException("UpdateException");
        }
    }

    @Override
    public void delete(String token) throws OtpDAOException {
        if(otpStore.containsKey(token)){
            otpStore.remove(token);
        } else {
            log.info("token {} doesn't exist for delete", token);
            throw new OtpDAOException("DeleteException");
        }
    }
}
