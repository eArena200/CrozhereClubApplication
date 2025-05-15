package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByClubAdmin_Id(Long clubAdminId);
}
