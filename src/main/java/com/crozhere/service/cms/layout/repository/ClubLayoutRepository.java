package com.crozhere.service.cms.layout.repository;

import com.crozhere.service.cms.layout.repository.entity.ClubLayout;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClubLayoutRepository extends MongoRepository<ClubLayout, String> {
    boolean existsByClubId(Long clubId);
}

