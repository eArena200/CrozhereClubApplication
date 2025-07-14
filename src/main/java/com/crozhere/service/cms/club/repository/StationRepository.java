package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByClub_Id(Long clubId);
    List<Station> findByClubIdIn(List<Long> clubIds);
}

