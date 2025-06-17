package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.RateCharge;
import com.crozhere.service.cms.club.repository.entity.ChargeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<RateCharge, Long> {
    List<RateCharge> findByRateIdAndChargeType(Long rateId, ChargeType chargeType);
    List<RateCharge> findByRateId(Long rateId);
}
