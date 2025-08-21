package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface StationRepository extends JpaRepository<Station, Long> {
    Set<Station> findByClub_Id(Long clubId);
    Set<Station> findByClubIdIn(Set<Long> clubIds);
}

