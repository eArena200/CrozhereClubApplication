package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, Long> {
    List<RateCard> findByClubId(Long clubId);
}
