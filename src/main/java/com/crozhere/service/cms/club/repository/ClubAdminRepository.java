package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubAdminRepository extends JpaRepository<ClubAdmin, Long> {
    Optional<ClubAdmin> findByUser_Id(Long userId);
}
