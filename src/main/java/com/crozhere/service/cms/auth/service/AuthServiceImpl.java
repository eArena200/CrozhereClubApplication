package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.InitAuthResponse;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.repository.UserDAO;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
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
            boolean isOtpValid = otpService.verifyOTP(
                    verifyAuthRequest.getToken(),
                    verifyAuthRequest.getOtp());

            if (!isOtpValid) {
                return VerifyAuthResponse.builder()
                        .isAllowed(false)
                        .build();
            }

            String identifier = otpService.getIdentifierForToken(verifyAuthRequest.getToken());

            User user = resolveOrCreateUser(identifier, verifyAuthRequest.getUserRole());

            String jwtToken = jwtService.generateToken(user);

            return VerifyAuthResponse.builder()
                    .isAllowed(true)
                    .jwt(jwtToken)
                    .userId(user.getId())
                    .build();

        } catch (OTPServiceException e) {
            log.error("OTP verification failed for token {}: {}", verifyAuthRequest.getToken(), e.getMessage());
            throw new AuthServiceException("VerifyAuthException: OTP verification failed", e);
        }
    }


    private User resolveOrCreateUser(String identifier, UserRole userRole) throws AuthServiceException {
        try {
            return userDAO.findUserByPhoneNumber(identifier);
        } catch (UserDAOException e) {
            // User not found → create new one
            User newUser = User.builder()
                    .id(UUID.randomUUID().toString())
                    .phoneNumber(identifier)
                    .userRole(userRole)
                    .build();

            try {
                userDAO.save(newUser);
                log.info("New user created: {}", newUser);
                return newUser;
            } catch (UserDAOException saveEx) {
                log.error("Failed to save new user for identifier {}: {}", identifier, saveEx.getMessage());
                throw new AuthServiceException("User creation failed", saveEx);
            }

        }
    }
}