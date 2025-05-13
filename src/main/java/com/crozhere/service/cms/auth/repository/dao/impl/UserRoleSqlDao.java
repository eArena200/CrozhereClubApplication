package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.UserRepository;
import com.crozhere.service.cms.auth.repository.UserRoleMappingRepository;
import com.crozhere.service.cms.auth.repository.dao.UserRoleDao;
import com.crozhere.service.cms.auth.repository.dao.exception.UserRoleDAOException;
import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRoleMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("UserRoleSqlDao")
public class UserRoleSqlDao implements UserRoleDao {

    private final UserRoleMappingRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserRoleSqlDao(UserRoleMappingRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(UserRoleMapping roleMapping) throws UserRoleDAOException {
        try {
            roleRepository.save(roleMapping);
        } catch (Exception e) {
            log.error("Failed to save role mapping: {}", roleMapping, e);
            throw new UserRoleDAOException("Error saving role mapping", e);
        }
    }

    @Override
    public UserRoleMapping get(Long roleId) throws UserRoleDAOException {
        try {
            return roleRepository.findById(roleId)
                    .orElseThrow(() -> new UserRoleDAOException("Role mapping not found: " + roleId));
        } catch (Exception e) {
            log.error("Failed to get role mapping: {}", roleId, e);
            throw new UserRoleDAOException("Error getting role mapping", e);
        }
    }

    @Override
    public void update(Long roleId, UserRoleMapping updatedRole) throws UserRoleDAOException {
        try {
            UserRoleMapping existing = get(roleId);
            existing.setRole(updatedRole.getRole());
            existing.setUser(updatedRole.getUser());
            roleRepository.save(existing);
        } catch (Exception e) {
            log.error("Failed to update role mapping: {}", roleId, e);
            throw new UserRoleDAOException("Error updating role mapping", e);
        }
    }

    @Override
    public void delete(Long roleId) throws UserRoleDAOException {
        try {
            roleRepository.deleteById(roleId);
        } catch (Exception e) {
            log.error("Failed to delete role mapping: {}", roleId, e);
            throw new UserRoleDAOException("Error deleting role mapping", e);
        }
    }

    @Override
    public List<UserRoleMapping> getRolesByUserId(Long userId) throws UserRoleDAOException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserRoleDAOException("User not found: " + userId));
            return roleRepository.findByUser(user);
        } catch (Exception e) {
            log.error("Failed to get roles for user: {}", userId, e);
            throw new UserRoleDAOException("Error getting roles by user ID", e);
        }
    }

    @Override
    public boolean hasRole(Long userId, String roleName) throws UserRoleDAOException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserRoleDAOException("User not found: " + userId));
            return roleRepository.findByUser(user).stream()
                    .anyMatch(role -> role.getRole().name().equalsIgnoreCase(roleName));
        } catch (Exception e) {
            log.error("Failed to check role '{}' for user: {}", roleName, userId, e);
            throw new UserRoleDAOException("Error checking role for user", e);
        }
    }
}
