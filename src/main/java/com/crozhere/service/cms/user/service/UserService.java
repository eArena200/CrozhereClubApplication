package com.crozhere.service.cms.user.service;

import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.repository.entity.UserRole;
import com.crozhere.service.cms.user.service.exception.UserServiceException;

import java.util.List;

public interface UserService {

    User getOrCreateUserByPhoneNumber(String phoneNumber, UserRole role) throws UserServiceException;

    User getUserById(Long userId) throws UserServiceException;

    User getUserByPhoneNumber(String phoneNumber) throws UserServiceException;

    User updateUser(Long userId, User updatedUser) throws UserServiceException;

    void deleteUser(Long userId) throws UserServiceException;

    boolean userHasRole(Long userId, UserRole role) throws UserServiceException;

    List<UserRole> getUserRoles(Long userId) throws UserServiceException;
}
