package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.dao.UserDao;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.dao.exception.UserDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("UserInMemDAO")
public class UserInMemDao implements UserDao {

    private final Map<Long, User> userStore;

    public UserInMemDao() {
        this.userStore = new HashMap<>();
    }

    @Override
    public void save(User user) throws UserDAOException {
        if (userStore.containsKey(user.getId())) {
            log.info("UserId {} already exists", user.getId());
            throw new UserDAOException("SaveException: User already exists");
        }

        userStore.put(user.getId(), user);
    }

    @Override
    public User get(Long userId) throws UserDAOException {
        if (userStore.containsKey(userId)) {
            return userStore.get(userId);
        } else {
            log.info("UserId {} doesn't exist", userId);
            throw new UserDAOException("GetException: User not found");
        }
    }

    @Override
    public void update(Long userId, User user) throws UserDAOException {
        if (userStore.containsKey(userId)) {
            userStore.put(userId, user);
        } else {
            log.info("UserId {} doesn't exist for update", userId);
            throw new UserDAOException("UpdateException: User not found");
        }
    }

    @Override
    public void delete(Long userId) throws UserDAOException {
        if (userStore.containsKey(userId)) {
            userStore.remove(userId);
        } else {
            log.info("UserId {} doesn't exist for delete", userId);
            throw new UserDAOException("DeleteException: User not found");
        }
    }

    @Override
    public User findUserByPhoneNumber(String phone) throws UserDAOException {
        List<User> users = userStore.values()
                .stream()
                .filter(user -> user.getPhone().equals(phone))
                .toList();

        if(users.isEmpty()){
            log.info("User with phone {} doesn't exist", phone);
            throw new UserDAOException("ReadException");
        }

        return users.get(0);
    }
}
