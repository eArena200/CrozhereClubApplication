package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.exception.UserDAOException;

public interface UserDAO {
    void save(User user) throws UserDAOException;
    User get(String userId) throws UserDAOException;
    void update(String userId, User user) throws UserDAOException;
    void delete(String userId) throws UserDAOException;

    User findUserByPhoneNumber(String phoneNumber) throws UserDAOException;
}
