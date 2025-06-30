package com.crozhere.service.cms.user.repository.dao;

import com.crozhere.service.cms.user.repository.entity.UserRole;
import com.crozhere.service.cms.user.repository.entity.UserRoles;
import com.crozhere.service.cms.user.repository.dao.exception.UserRoleDAOException;

import java.util.List;

public interface UserRoleDao {

    void save(UserRoles roleMapping) throws UserRoleDAOException;

    UserRoles get(Long roleId) throws UserRoleDAOException;

    void update(Long roleId, UserRoles updatedRole) throws UserRoleDAOException;

    void delete(Long roleId) throws UserRoleDAOException;

    List<UserRoles> getRolesByUserId(Long userId) throws UserRoleDAOException;

    boolean hasRole(Long userId, UserRole role) throws UserRoleDAOException;
}

