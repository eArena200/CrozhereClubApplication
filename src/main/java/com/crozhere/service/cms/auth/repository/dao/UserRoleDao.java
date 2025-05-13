package com.crozhere.service.cms.auth.repository.dao;

import com.crozhere.service.cms.auth.repository.entity.UserRoleMapping;
import com.crozhere.service.cms.auth.repository.dao.exception.UserRoleDAOException;

import java.util.List;

public interface UserRoleDao {

    void save(UserRoleMapping roleMapping) throws UserRoleDAOException;

    UserRoleMapping get(Long roleId) throws UserRoleDAOException;

    void update(Long roleId, UserRoleMapping updatedRole) throws UserRoleDAOException;

    void delete(Long roleId) throws UserRoleDAOException;

    List<UserRoleMapping> getRolesByUserId(Long userId) throws UserRoleDAOException;

    boolean hasRole(Long userId, String roleName) throws UserRoleDAOException;
}

