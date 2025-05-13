package com.crozhere.service.cms.auth.repository.dao;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.dao.exception.UserDAOException;

public interface UserDao {
    void save(User user) throws UserDAOException;
    User get(Long userId) throws UserDAOException;
    void update(Long userId, User user) throws UserDAOException;
    void delete(Long userId) throws UserDAOException;

    User findUserByPhoneNumber(String phoneNumber) throws UserDAOException;
}
