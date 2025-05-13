package com.crozhere.service.cms.auth.repository.dao.impl;

import com.crozhere.service.cms.auth.repository.dao.UserRoleDao;
import com.crozhere.service.cms.auth.repository.entity.UserRoleMapping;
import com.crozhere.service.cms.auth.repository.dao.exception.UserRoleDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("UserRoleInMemDao")
public class UserRoleInMemDao implements UserRoleDao {

    private final Map<String, UserRoleMapping> roleStore;

    public UserRoleInMemDao() {
        this.roleStore = new HashMap<>();
    }

    @Override
    public void save(UserRoleMapping roleMapping) throws UserRoleDAOException {
        if (roleStore.containsKey(roleMapping.getId())) {
            log.info("RoleId {} already exists", roleMapping.getId());
            throw new UserRoleDAOException("SaveException: Role already exists");
        }
        roleStore.put(roleMapping.getId(), roleMapping);
    }

    @Override
    public UserRoleMapping get(String roleId) throws UserRoleDAOException {
        if (roleStore.containsKey(roleId)) {
            return roleStore.get(roleId);
        } else {
            log.info("RoleId {} doesn't exist", roleId);
            throw new UserRoleDAOException("GetException: Role not found");
        }
    }

    @Override
    public void update(String roleId, UserRoleMapping updatedRole) throws UserRoleDAOException {
        if (roleStore.containsKey(roleId)) {
            roleStore.put(roleId, updatedRole);
        } else {
            log.info("RoleId {} doesn't exist for update", roleId);
            throw new UserRoleDAOException("UpdateException: Role not found");
        }
    }

    @Override
    public void delete(String roleId) throws UserRoleDAOException {
        if (roleStore.containsKey(roleId)) {
            roleStore.remove(roleId);
        } else {
            log.info("RoleId {} doesn't exist for delete", roleId);
            throw new UserRoleDAOException("DeleteException: Role not found");
        }
    }

    @Override
    public List<UserRoleMapping> getRolesByUserId(String userId) throws UserRoleDAOException {
        List<UserRoleMapping> roles = roleStore.values().stream()
                .filter(role -> role.getUser() != null && role.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            log.info("No roles found for userId {}", userId);
        }

        return roles;
    }

    @Override
    public boolean hasRole(String userId, String roleName) throws UserRoleDAOException {
        return roleStore.values().stream()
                .anyMatch(role ->
                        role.getUser() != null &&
                                role.getUser().getId().equals(userId) &&
                                role.getRole().name().equalsIgnoreCase(roleName)
                );
    }
}
