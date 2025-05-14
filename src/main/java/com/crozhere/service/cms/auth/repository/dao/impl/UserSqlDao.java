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
            log.error("Failed to save player: {}", user.getPhone(), e);
            throw new UserDAOException("Error saving player", e);
        }
    }

    @Override
    public User get(Long userId) throws UserDAOException {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UserDAOException("User not found with id: " + userId));
        } catch (Exception e) {
            log.error("Failed to get player by ID: {}", userId, e);
            throw new UserDAOException("Error getting player", e);
        }
    }

    @Override
    public void update(Long userId, User updatedUser) throws UserDAOException {
        try {
            User existingUser = get(userId);
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setActive(updatedUser.isActive());
            userRepository.save(existingUser);
        } catch (Exception e) {
            log.error("Failed to update player: {}", userId, e);
            throw new UserDAOException("Error updating player", e);
        }
    }

    @Override
    public void delete(Long userId) throws UserDAOException {
        try {
            Optional<User> user = userRepository.findById(userId);
            user.ifPresent(userRepository::delete);
        } catch (Exception e) {
            log.error("Failed to delete player with ID: {}", userId, e);
            throw new UserDAOException("Error deleting player", e);
        }
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) throws UserDAOException {
        try {
            return userRepository.findByPhone(phoneNumber)
                    .orElseThrow(() -> new UserDAOException("User not found with phone number: " + phoneNumber));
        } catch (Exception e) {
            log.error("Failed to find player by phone number: {}", phoneNumber, e);
            throw new UserDAOException("Error finding player by phone number", e);
        }
    }
}
