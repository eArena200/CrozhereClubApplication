package com.crozhere.service.cms.auth.service;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import com.crozhere.service.cms.auth.service.exception.UserServiceException;

import java.util.List;

public interface UserService {

    User getOrCreateUserByPhoneNumber(String phoneNumber, UserRole role) throws UserServiceException;

    User getUserById(Long userId) throws UserServiceException;

    User getUserByPhoneNumber(String phoneNumber) throws UserServiceException;

    User updateUser(Long userId, User updatedUser) throws UserServiceException;

    void deleteUser(Long userId) throws UserServiceException;

    void assignRoleToUser(Long userId, UserRole role) throws UserServiceException;

    void removeRoleFromUser(Long userId, UserRole role) throws UserServiceException;

    boolean userHasRole(Long userId, UserRole role) throws UserServiceException;

    List<UserRole> getUserRoles(Long userId) throws UserServiceException;
}
