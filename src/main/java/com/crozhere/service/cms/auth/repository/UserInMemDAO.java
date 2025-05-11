package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.exception.UserDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("UserInMemDAO")
public class UserInMemDAO implements UserDAO {

    private final Map<String, User> userStore;

    public UserInMemDAO() {
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
    public User get(String userId) throws UserDAOException {
        if (userStore.containsKey(userId)) {
            return userStore.get(userId);
        } else {
            log.info("UserId {} doesn't exist", userId);
            throw new UserDAOException("GetException: User not found");
        }
    }

    @Override
    public void update(String userId, User user) throws UserDAOException {
        if (userStore.containsKey(userId)) {
            userStore.put(userId, user);
        } else {
            log.info("UserId {} doesn't exist for update", userId);
            throw new UserDAOException("UpdateException: User not found");
        }
    }

    @Override
    public void delete(String userId) throws UserDAOException {
        if (userStore.containsKey(userId)) {
            userStore.remove(userId);
        } else {
            log.info("UserId {} doesn't exist for delete", userId);
            throw new UserDAOException("DeleteException: User not found");
        }
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) throws UserDAOException {
        List<User> users = userStore.values()
                .stream()
                .filter(user -> user.getPhoneNumber().equals(phoneNumber))
                .toList();

        if(users.isEmpty()){
            log.info("User with phoneNumber {} doesn't exist", phoneNumber);
            throw new UserDAOException("ReadException");
        }

        return users.get(0);
    }
}
