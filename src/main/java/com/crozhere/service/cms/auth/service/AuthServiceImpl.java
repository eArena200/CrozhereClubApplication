package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.InitAuthResponse;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.repository.UserDAO;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.exception.UserDAOException;
import com.crozhere.service.cms.auth.service.exception.AuthServiceException;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO;
    private final OTPService otpService;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserDAO userDAO,
            OTPService otpService,
            JwtService jwtService){
        this.userDAO = userDAO;
        this.otpService = otpService;
        this.jwtService = jwtService;
    }

    @Override
    public InitAuthResponse initAuth(InitAuthRequest initAuthRequest)
            throws AuthServiceException {
        try {
            String token = otpService.sendOTP(initAuthRequest.getIdentifier());
            return InitAuthResponse.builder()
                    .token(token)
                    .build();
        } catch (OTPServiceException otpServiceException){
            log.error("Exception while sending OTP for identifier: {}", initAuthRequest.getIdentifier());
            throw new AuthServiceException("InitAuthException");
        }
    }

    @Override
    public VerifyAuthResponse verifyAuth(VerifyAuthRequest verifyAuthRequest)
            throws AuthServiceException {

        try {
            if(!otpService.verifyOTP(verifyAuthRequest.getToken(),
                    verifyAuthRequest.getOtp())){
                return VerifyAuthResponse.builder()
                        .isAllowed(false)
                        .build();
            }

            String identifier =
                    otpService.getIdentifierForToken(verifyAuthRequest.getToken());

            User user;
            try {
                user = userDAO.findUserByPhoneNumber(identifier);
            } catch (UserDAOException userDAOException){
                user = User.builder()
                        .id(UUID.randomUUID().toString())
                        .phoneNumber(identifier)
                        .userRole(verifyAuthRequest.getUserRole())
                        .build();

                try {
                    userDAO.save(user);
                } catch (UserDAOException e){
                    log.error("Exception in saving the user: {}", user);
                    throw new AuthServiceException("VerifyAuthException");
                }
            }

            log.info("New User created: {}", user.toString());
            String jwtToken = jwtService.generateToken(user);

            return VerifyAuthResponse.builder()
                    .isAllowed(true)
                    .jwt(jwtToken)
                    .userId(user.getId())
                    .build();
        } catch (OTPServiceException otpServiceException) {
            log.info("Exception in OTPService for token: {}", verifyAuthRequest.getToken());
            throw new AuthServiceException("VerifyAuthException");
        }
    }
}
