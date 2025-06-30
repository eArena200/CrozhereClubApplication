package com.crozhere.service.cms.auth.service.impl;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.repository.entity.UserRole;
import com.crozhere.service.cms.auth.service.AuthService;
import com.crozhere.service.cms.auth.service.OTPService;
import com.crozhere.service.cms.user.service.UserService;
import com.crozhere.service.cms.auth.service.exception.AuthServiceException;
import com.crozhere.service.cms.auth.service.exception.AuthServiceExceptionType;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import com.crozhere.service.cms.user.service.exception.UserServiceException;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import com.crozhere.service.cms.user.service.ClubAdminService;
import com.crozhere.service.cms.user.repository.entity.Player;
import com.crozhere.service.cms.user.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final OTPService otpService;
    private final JwtService jwtService;
    private final UserService userService;
    private final PlayerService playerService;
    private final ClubAdminService clubAdminService;

    @Autowired
    public AuthServiceImpl(
            OTPService otpService,
            JwtService jwtService,
            UserService userService,
            PlayerService playerService,
            ClubAdminService clubAdminService) {
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.playerService = playerService;
        this.clubAdminService = clubAdminService;
    }

    @Override
    public void initAuth(InitAuthRequest request) throws AuthServiceException {
        try {
            otpService.sendOTP(request.getPhone());
        } catch (OTPServiceException e) {
            log.error("Failed to initialize auth for phone: {}", request.getPhone(), e);
            throw new AuthServiceException(
                    AuthServiceExceptionType.INIT_AUTH_FAILED);
        }
    }

    @Override
    public VerifyAuthResponse verifyAuth(VerifyAuthRequest request) throws AuthServiceException {
        try {
            boolean valid = otpService.verifyOTP(request.getPhone(), request.getOtp());
            if (!valid) {
                throw new AuthServiceException(
                        AuthServiceExceptionType.INVALID_OTP);
            }
            return createUserAndToken(request);

        } catch (AuthServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Verify auth failed: {}", request.getPhone(), e);
            throw new AuthServiceException(
                    AuthServiceExceptionType.VERIFY_AUTH_FAILED, e);
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    protected VerifyAuthResponse createUserAndToken(VerifyAuthRequest request)
            throws UserServiceException {

        User user;
        try {
            user = userService.getOrCreateUserByPhoneNumber(
                    request.getPhone(), request.getRole());

        } catch (UserServiceException e) {
            log.error("Exception in getOrCreateUser for request: {}", request, e);
            throw e;
        }

        String token;
        try {
            token = jwtService.generateToken(
                        user.getId(),
                        user.getRoles()
                                .stream()
                                .map(userRole -> userRole.getRole().name())
                                .toList());
        } catch (Exception e) {
            throw new AuthServiceException(AuthServiceExceptionType.GENERATE_TOKEN_FAILED);
        }

        if (request.getRole().equals(UserRole.PLAYER)) {
            try {
                Player player = playerService.getPlayerByUserId(user.getId());
                return VerifyAuthResponse.builder()
                        .jwt(token)
                        .userId(user.getId())
                        .role(UserRole.PLAYER)
                        .playerId(player.getId())
                        .build();
            } catch (Exception e) {
                log.warn("User has PLAYER role but player record not found for user ID: {}", user.getId());
                throw new AuthServiceException(
                        AuthServiceExceptionType.GET_PLAYER_PROFILE_FAILED);
            }
        }

        if (request.getRole().equals(UserRole.CLUB_ADMIN)) {
            try {
                ClubAdmin clubAdmin = clubAdminService.getClubAdminByUserId(user.getId());
                return VerifyAuthResponse.builder()
                        .jwt(token)
                        .userId(user.getId())
                        .role(UserRole.CLUB_ADMIN)
                        .clubAdminId(clubAdmin.getId())
                        .build();
            } catch (Exception e) {
                log.warn("User has CLUB_ADMIN role but clubAdmin record not found for user ID: {}", user.getId());
                throw new AuthServiceException(
                        AuthServiceExceptionType.GET_CLUB_ADMIN_PROFILE_FAILED);
            }
        }

        return VerifyAuthResponse.builder()
                .jwt(token)
                .userId(user.getId())
                .role(UserRole.GUEST)
                .build();
    }
}
