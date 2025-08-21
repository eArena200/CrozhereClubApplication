package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, Long> {
    Set<RateCard> findByClubId(Long clubId);

    @Query("""
        select distinct rc from RateCard rc
        left join fetch rc.rates r
        left join fetch r.rateCharges ch
        where rc.id = :rateCardId
    """)
    Optional<RateCard> findDetailedRateCardById(@Param("rateCardId") Long rateCardId);

    @Query("""
        select distinct rc from RateCard rc
        left join fetch rc.rates r
        left join fetch r.rateCharges ch
        where rc.id in :rateCardIds
    """)
    Set<RateCard> getDetailedRateCardsByIds(@Param("rateCardIds") Set<Long> rateCardIds);


    @Query("""
        select distinct rc from RateCard rc
        left join fetch rc.rates r
        left join fetch r.rateCharges ch
        where rc.club.id = :clubId
    """)
    Set<RateCard> getDetailedRateCardsByClubId(@Param("clubId") Long clubId);
}
