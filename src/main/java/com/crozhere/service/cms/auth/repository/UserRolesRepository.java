package com.crozhere.service.cms.auth.repository;

import com.crozhere.service.cms.auth.repository.entity.UserRoles;
import com.crozhere.service.cms.auth.repository.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {
    List<UserRoles> findByUserId(Long userId);
    boolean existsByUserIdAndRole(Long userId, UserRole role);
}
