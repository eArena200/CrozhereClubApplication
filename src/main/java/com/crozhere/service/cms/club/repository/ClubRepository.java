package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ClubRepository extends JpaRepository<Club, Long> {
    Set<Club> findByClubAdminId(Long clubAdminId);

    @Query("""
        select distinct c from Club c
        left join fetch c.stations s
        left join fetch c.rateCards rc
        left join fetch rc.rates r
        left join fetch r.rateCharges ch
        where c.id = :clubId
    """)
    Optional<Club> findDetailedClubById(@Param("clubId") Long clubId);

}
