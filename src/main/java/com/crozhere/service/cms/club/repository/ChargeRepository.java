package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.RateCharge;
import com.crozhere.service.cms.club.repository.entity.ChargeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ChargeRepository extends JpaRepository<RateCharge, Long> {
    Set<RateCharge> findByRateIdAndChargeType(Long rateId, ChargeType chargeType);
    Set<RateCharge> findByRateId(Long rateId);
}
