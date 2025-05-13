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
import com.crozhere.service.cms.auth.service.exception.OTPServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final OTPService otpService;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(
            @Qualifier("UserSqlDao") UserDao userDao,
            @Qualifier("UserRoleSqlDao") UserRoleDao userRoleDao,
            OTPService otpService,
            JwtService jwtService){
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
        this.otpService = otpService;
        this.jwtService = jwtService;
    }

    @Override
    public void initAuth(InitAuthRequest request) throws AuthServiceException {
        try {
            User user;
            try {
                user = userDao.findUserByPhoneNumber(request.getPhone());
            } catch (UserDAOException e) {
                user = User.builder()
                        .id(UUID.randomUUID().toString())
                        .phone(request.getPhone())
                        .isActive(true)
                        .build();

                userDao.save(user);
            }

            if (!userRoleDao.hasRole(user.getId(), request.getRole().name())) {
                UserRoleMapping roleMapping = UserRoleMapping.builder()
                        .id(UUID.randomUUID().toString())
                        .user(user)
                        .role(request.getRole())
                        .build();

                userRoleDao.save(roleMapping);
            }

            otpService.sendOTP(request.getPhone());

        } catch (UserDAOException | UserRoleDAOException | OTPServiceException e) {
            log.error("Failed to initialize auth for phone: {}", request.getPhone(), e);
            throw new AuthServiceException("InitAuthException", e);
        }
    }

    @Override
    public VerifyAuthResponse verifyAuth(VerifyAuthRequest request) throws AuthServiceException {
        try {
            boolean valid = otpService.verifyOTP(request.getPhone(), request.getOtp());
            if (!valid) {
                throw new AuthServiceException("Invalid OTP");
            }

            User user = userDao.findUserByPhoneNumber(request.getPhone());
            List<UserRoleMapping> roleMappings = userRoleDao.getRolesByUserId(user.getId());
            List<UserRole> roles = roleMappings.stream()
                    .map(UserRoleMapping::getRole)
                    .collect(Collectors.toList());

            String token =
                    jwtService.generateToken(user.getId(),
                            roles.stream().map(Enum::name).toList());

            return VerifyAuthResponse.builder()
                    .jwt(token)
                    .userId(user.getId())
                    .roles(roles)
                    .build();

        } catch (OTPServiceException | UserDAOException | UserRoleDAOException e) {
            log.error("Failed to verify auth for phone: {}", request.getPhone(), e);
            throw new AuthServiceException("VerifyAuthException", e);
        }
    }
}
