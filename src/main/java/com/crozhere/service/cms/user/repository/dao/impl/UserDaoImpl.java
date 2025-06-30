package com.crozhere.service.cms.user.repository.dao.impl;

import com.crozhere.service.cms.user.repository.UserRepository;
import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.repository.dao.UserDao;
import com.crozhere.service.cms.user.repository.dao.exception.UserDAOException;
import com.crozhere.service.cms.user.repository.dao.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Override
    public void save(User user) throws UserDAOException {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Exception while saving user: {}", user.toString(), e);
            throw new UserDAOException("Failed to save user", e);
        }
    }

    @Override
    public User get(Long userId) throws UserDAOException {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("User not found with id: " + userId));
        } catch (DataNotFoundException e) {
            log.info("User not found with userId: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while getting user with userId: {}", userId, e);
            throw new UserDAOException("Failed to get user", e);
        }
    }

    @Override
    public void update(Long userId, User updatedUser) throws UserDAOException {
        try {
            User existingUser = get(userId);
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setActive(updatedUser.isActive());
            userRepository.save(existingUser);
        } catch (DataNotFoundException e) {
            log.info("User not found for update with userId: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating the user with userId: {}", userId, e);
            throw new UserDAOException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Long userId) throws UserDAOException {
        try {
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
            } else {
                throw new DataNotFoundException("User not found with id: " + userId);
            }
        } catch (DataNotFoundException e) {
            log.info("User not found for delete with userId: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while deleting user with userId: {}", userId, e);
            throw new UserDAOException("Failed to delete user", e);
        }
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) throws UserDAOException {
        try {
            return userRepository.findByPhone(phoneNumber)
                    .orElseThrow(() -> new DataNotFoundException("User not found with phone number: " + phoneNumber));
        } catch (DataNotFoundException e) {
            log.info("User not found with phone-number: {}", phoneNumber);
            throw e;
        } catch (Exception e) {
            log.error("Exception while getting user for phone-number: {}", phoneNumber, e);
            throw new UserDAOException("Failed to find user by phone number: " + phoneNumber, e);
        }
    }
}
