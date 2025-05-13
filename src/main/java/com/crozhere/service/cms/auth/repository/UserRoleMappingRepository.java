package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.auth.repository.entity.UserRoleMapping;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, String> {
    List<UserRoleMapping> findByUser(User user);
    boolean existsByUserAndRole(User user, UserRole role);
}
