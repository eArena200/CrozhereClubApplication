package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.UserRolesRepository;
import com.crozhere.service.cms.auth.repository.entity.UserRoles;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import com.crozhere.service.cms.auth.repository.dao.UserRoleDao;
import com.crozhere.service.cms.auth.repository.dao.exception.UserRoleDAOException;
import com.crozhere.service.cms.auth.repository.dao.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserRoleDaoImpl implements UserRoleDao {

    private final UserRolesRepository userRolesRepository;

    @Autowired
    public UserRoleDaoImpl(UserRolesRepository userRolesRepository) {
        this.userRolesRepository = userRolesRepository;
    }

    @Override
    public void save(UserRoles roleMapping) throws UserRoleDAOException {
        try {
            userRolesRepository.save(roleMapping);
        } catch (Exception e) {
            log.error("Exception while saving UserRole: {}", roleMapping, e);
            throw new UserRoleDAOException("Failed to save user role", e);
        }
    }

    @Override
    public UserRoles get(Long roleId) throws UserRoleDAOException {
        try {
            return userRolesRepository.findById(roleId)
                    .orElseThrow(() -> new DataNotFoundException("User role not found with id: " + roleId));
        } catch (DataNotFoundException e) {
            log.info("User role not found with id: {}", roleId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while getting UserRole with id: {}", roleId, e);
            throw new UserRoleDAOException("Failed to get user role", e);
        }
    }

    @Override
    public void update(Long roleId, UserRoles updatedRole) throws UserRoleDAOException {
        try {
            UserRoles existingRole = get(roleId);
            existingRole.setRole(updatedRole.getRole());
            existingRole.setUser(updatedRole.getUser());
            userRolesRepository.save(existingRole);
        } catch (DataNotFoundException e) {
            log.info("User role not found for update with id: {}", roleId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating UserRole with id: {}", roleId, e);
            throw new UserRoleDAOException("Failed to update user role", e);
        }
    }

    @Override
    public void delete(Long roleId) throws UserRoleDAOException {
        try {
            if (userRolesRepository.existsById(roleId)) {
                userRolesRepository.deleteById(roleId);
            } else {
                throw new DataNotFoundException("User role not found with id: " + roleId);
            }
        } catch (DataNotFoundException e) {
            log.info("User role not found for delete with id: {}", roleId);
            throw e;
        } catch (Exception e) {
            log.error("Exception while deleting UserRole with id: {}", roleId, e);
            throw new UserRoleDAOException("Failed to delete user role", e);
        }
    }

    @Override
    public List<UserRoles> getRolesByUserId(Long userId) throws UserRoleDAOException {
        try {
            return userRolesRepository.findByUserId(userId);
        } catch (Exception e) {
            log.error("Exception while getting roles for userId: {}", userId, e);
            throw new UserRoleDAOException("Failed to get roles by user id", e);
        }
    }

    @Override
    public boolean hasRole(Long userId, UserRole role) throws UserRoleDAOException {
        try {
            return userRolesRepository.existsByUserIdAndRole(userId, role);
        } catch (Exception e) {
            log.error("Exception while checking if userId {} has role {}", userId, role, e);
            throw new UserRoleDAOException("Failed to check user role", e);
        }
    }
}
