package com.crozhere.service.cms.auth.repository.dao;

import com.crozhere.service.cms.auth.repository.entity.UserRoleMapping;
import com.crozhere.service.cms.auth.repository.dao.exception.UserRoleDAOException;

import java.util.List;

public interface UserRoleDao {

    void save(UserRoleMapping roleMapping) throws UserRoleDAOException;

    UserRoleMapping get(String roleId) throws UserRoleDAOException;

    void update(String roleId, UserRoleMapping updatedRole) throws UserRoleDAOException;

    void delete(String roleId) throws UserRoleDAOException;

    List<UserRoleMapping> getRolesByUserId(String userId) throws UserRoleDAOException;

    boolean hasRole(String userId, String roleName) throws UserRoleDAOException;
}

