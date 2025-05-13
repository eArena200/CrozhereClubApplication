package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.UserRepository;
import com.crozhere.service.cms.auth.repository.dao.UserDao;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.dao.exception.UserDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("UserSqlDao")
public class UserSqlDao implements UserDao {

    private final UserRepository userRepository;

    @Autowired
    public UserSqlDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) throws UserDAOException {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to save user: {}", user.getPhone(), e);
            throw new UserDAOException("Error saving user", e);
        }
    }

    @Override
    public User get(String userId) throws UserDAOException {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UserDAOException("User not found with id: " + userId));
        } catch (Exception e) {
            log.error("Failed to get user by ID: {}", userId, e);
            throw new UserDAOException("Error getting user", e);
        }
    }

    @Override
    public void update(String userId, User updatedUser) throws UserDAOException {
        try {
            User existingUser = get(userId);
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setActive(updatedUser.isActive());
            userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Failed to update user: {}", userId, e);
            throw new UserDAOException("Error updating user", e);
        }
    }

    @Override
    public void delete(String userId) throws UserDAOException {
        try {
            Optional<User> user = userRepository.findById(userId);
            user.ifPresent(userRepository::delete);
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", userId, e);
            throw new UserDAOException("Error deleting user", e);
        }
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) throws UserDAOException {
        try {
            return userRepository.findByPhone(phoneNumber)
                    .orElseThrow(() -> new UserDAOException("User not found with phone number: " + phoneNumber));
        } catch (Exception e) {
            log.error("Failed to find user by phone number: {}", phoneNumber, e);
            throw new UserDAOException("Error finding user by phone number", e);
        }
    }
}
