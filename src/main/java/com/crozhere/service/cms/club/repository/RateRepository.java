package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByRateCardId(Long rateCardId);
}
