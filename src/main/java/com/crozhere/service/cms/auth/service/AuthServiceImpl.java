package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.controller.model.request.InitAuthRequest;
import com.crozhere.service.cms.auth.controller.model.request.VerifyAuthRequest;
import com.crozhere.service.cms.auth.controller.model.response.VerifyAuthResponse;
import com.crozhere.service.cms.auth.repository.dao.UserDao;
import com.crozhere.service.cms.auth.repository.dao.UserRoleDao;
import com.crozhere.service.cms.auth.repository.dao.exception.UserDAOException;
import com.crozhere.service.cms.auth.repository.dao.exception.UserRoleDAOException;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import com.crozhere.service.cms.auth.repository.entity.UserRoleMapping;
import com.crozhere.service.cms.auth.service.exception.AuthServiceException;
import com.crozhere.service.cms.auth.service.exception.AuthServiceExceptionType;
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import com.crozhere.service.cms.club.service.ClubAdminService;
import com.crozhere.service.cms.player.repository.entity.Player;
import com.crozhere.service.cms.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final OTPService otpService;
    private final JwtService jwtService;
    private final PlayerService playerService;
    private final ClubAdminService clubAdminService;

    @Autowired
    public AuthServiceImpl(
            @Qualifier("UserSqlDao") UserDao userDao,
            @Qualifier("UserRoleSqlDao") UserRoleDao userRoleDao,
            OTPService otpService,
            JwtService jwtService,
            PlayerService playerService,
            ClubAdminService clubAdminService) {
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
        this.otpService = otpService;
        this.jwtService = jwtService;
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
            throws UserDAOException, UserRoleDAOException {

        User user;
        try {
            user = userDao.findUserByPhoneNumber(request.getPhone());
        } catch (UserDAOException e) {
            user = User.builder()
                    .phone(request.getPhone())
                    .isActive(true)
                    .build();
            userDao.save(user);
        }

        boolean isNewRole = false;
        if (!userRoleDao.hasRole(user.getId(), request.getRole().name())) {
            UserRoleMapping roleMapping = UserRoleMapping.builder()
                    .user(user)
                    .role(request.getRole())
                    .build();
            userRoleDao.save(roleMapping);
            isNewRole = true;
        }

        if (isNewRole) {
            try {
                if (request.getRole() == UserRole.PLAYER) {
                    playerService.createPlayerForUser(user);
                } else if (request.getRole() == UserRole.CLUB_ADMIN) {
                    clubAdminService.createClubAdminForUser(user);
                }
            } catch (Exception e) {
                log.error("Failed to create role-based profile for user ID: {}", user.getId(), e);
                if (request.getRole() == UserRole.PLAYER) {
                    throw new AuthServiceException(
                            AuthServiceExceptionType.CREATE_PLAYER_FAILED);
                } else if (request.getRole() == UserRole.CLUB_ADMIN) {
                    throw new AuthServiceException(
                            AuthServiceExceptionType.CREATE_CLUB_ADMIN_FAILED);
                }
            }
        }

        List<UserRoleMapping> roleMappings = userRoleDao.getRolesByUserId(user.getId());
        List<UserRole> roles = roleMappings.stream()
                .map(UserRoleMapping::getRole)
                .collect(Collectors.toList());

        String token;
        try {
            token = jwtService.generateToken(
                    user.getId(),
                    roles.stream().map(Enum::name).toList()
            );
        } catch (Exception e) {
            throw new AuthServiceException(AuthServiceExceptionType.GENERATE_TOKEN_FAILED);
        }

        if (request.getRole().equals(UserRole.PLAYER)) {
            try {
                Player player = playerService.getPlayerByUserId(user.getId());
                return VerifyAuthResponse.builder()
                        .jwt(token)
                        .userId(user.getId())
                        .roles(roles)
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
                        .roles(roles)
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
                .roles(roles)
                .build();
    }
}
