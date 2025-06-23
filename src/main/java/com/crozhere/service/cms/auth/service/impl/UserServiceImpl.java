package com.crozhere.service.cms.auth.service.impl;

import com.crozhere.service.cms.auth.repository.dao.UserDao;
import com.crozhere.service.cms.auth.repository.dao.UserRoleDao;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import com.crozhere.service.cms.auth.repository.entity.UserRoles;
import com.crozhere.service.cms.auth.service.UserService;
import com.crozhere.service.cms.auth.service.exception.UserServiceException;
import com.crozhere.service.cms.auth.service.exception.UserServiceExceptionType;
import com.crozhere.service.cms.club.service.ClubAdminService;
import com.crozhere.service.cms.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;

    private final PlayerService playerService;
    private final ClubAdminService clubAdminService;

    @Autowired
    public UserServiceImpl(
            UserDao userDao,
            UserRoleDao userRoleDao,
            PlayerService playerService,
            ClubAdminService clubAdminService) {
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
        this.playerService = playerService;
        this.clubAdminService = clubAdminService;
    }

    @Override
    @Transactional
    public User getOrCreateUserByPhoneNumber(String phoneNumber, UserRole role) throws UserServiceException {
        try {
            User existingUser = userDao.findUserByPhoneNumber(phoneNumber);
            if (!userRoleDao.hasRole(existingUser.getId(), role)) {
                UserRoles userRole = UserRoles.builder()
                        .user(existingUser)
                        .role(role)
                        .build();

                userRoleDao.save(userRole);
                switch (role) {
                    case PLAYER -> playerService.createPlayerForUser(existingUser);
                    case CLUB_ADMIN -> clubAdminService.createClubAdminForUser(existingUser);
                }

                existingUser.getRoles().add(userRole);
            } else {
                log.info("User {} already has role {}", existingUser.getId(), role);
            }
            return existingUser;

        } catch (DataNotFoundException e) {
            User newUser = User.builder()
                    .phone(phoneNumber)
                    .roles(new ArrayList<>())
                    .isActive(true)
                    .build();
            userDao.save(newUser);

            UserRoles userRole = UserRoles.builder()
                    .user(newUser)
                    .role(role)
                    .build();
            userRoleDao.save(userRole);

            switch (role) {
                case PLAYER -> playerService.createPlayerForUser(newUser);
                case CLUB_ADMIN -> clubAdminService.createClubAdminForUser(newUser);
            }
            newUser.getRoles().add(userRole);
            return newUser;
        } catch (Exception e) {
            log.error("Exception while creating user for phone-number {} and role {}", phoneNumber, role, e);
            throw new UserServiceException(UserServiceExceptionType.CREATE_USER_FAILED);
        }
    }

    @Override
    public User getUserById(Long userId) throws UserServiceException {
        try {
            return userDao.get(userId);
        } catch (DataNotFoundException e) {
            log.info("User not found by userId: {}", userId);
            throw new UserServiceException(UserServiceExceptionType.USER_NOT_FOUND);
        }  catch (Exception e) {
            log.error("Failed to get user by ID: {}", userId, e);
            throw new UserServiceException(UserServiceExceptionType.GET_USER_FAILED);
        }
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) throws UserServiceException {
        try {
            return userDao.findUserByPhoneNumber(phoneNumber);
        } catch (DataNotFoundException e) {
            log.info("User not found for phone-number: {}", phoneNumber);
            throw new UserServiceException(UserServiceExceptionType.USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("Failed to get user by phone number: {}", phoneNumber, e);
            throw new UserServiceException(UserServiceExceptionType.GET_USER_FAILED);
        }
    }

    @Override
    public User updateUser(Long userId, User updatedUser) throws UserServiceException {
        try {
            userDao.update(userId, updatedUser);
            return userDao.get(userId);
        } catch (DataNotFoundException e) {
            log.info("User not found for update with userId: {}", userId);
            throw new UserServiceException(UserServiceExceptionType.USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userId, e);
            throw new UserServiceException(UserServiceExceptionType.UPDATE_USER_FAILED);
        }
    }

    @Override
    public void deleteUser(Long userId) throws UserServiceException {
        try {
            userDao.delete(userId);
        } catch (DataNotFoundException e) {
            log.info("User not found for delete with userId: {}", userId);
            throw new UserServiceException(UserServiceExceptionType.USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userId, e);
            throw new UserServiceException(UserServiceExceptionType.DELETE_USER_FAILED);
        }
    }

    @Override
    public void assignRoleToUser(Long userId, UserRole role) throws UserServiceException {
        try {
            if (!userRoleDao.hasRole(userId, role)) {
                User user = userDao.get(userId);
                UserRoles userRole = UserRoles.builder()
                        .user(user)
                        .role(role)
                        .build();
                userRoleDao.save(userRole);
                log.info("Assigned role {} to user {}", role, userId);
            } else {
                log.info("User {} already has role {}", userId, role);
            }
        } catch (DataNotFoundException e) {
            log.info("User not found for role-assignment with userId: {}", userId);
            throw new UserServiceException(UserServiceExceptionType.USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("Failed to assign role {} to user {}", role, userId, e);
            throw new UserServiceException(UserServiceExceptionType.ASSIGN_ROLE_FAILED);
        }
    }

    @Override
    public void removeRoleFromUser(Long userId, UserRole role) throws UserServiceException {
        try {
            List<UserRoles> roles = userRoleDao.getRolesByUserId(userId);
            roles.stream()
                    .filter(r -> r.getRole() == role)
                    .findFirst()
                    .ifPresent(r -> {
                        try {
                            userRoleDao.delete(r.getId());
                            log.info("Removed role {} from user {}", role, userId);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to remove role {} from user {}", role, userId, e);
            throw new UserServiceException(UserServiceExceptionType.REMOVE_ROLE_FAILED);
        }
    }

    @Override
    public boolean userHasRole(Long userId, UserRole role) throws UserServiceException {
        try {
            return userRoleDao.hasRole(userId, role);
        } catch (Exception e) {
            log.error("Failed to check role {} for user {}", role, userId, e);
            throw new UserServiceException(UserServiceExceptionType.CHECK_ROLE_FAILED);
        }
    }

    @Override
    public List<UserRole> getUserRoles(Long userId) throws UserServiceException {
        try {
            return userRoleDao.getRolesByUserId(userId).stream()
                    .map(UserRoles::getRole)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get roles for user {}", userId, e);
            throw new UserServiceException(UserServiceExceptionType.GET_USER_ROLES_FAILED);
        }
    }
}
