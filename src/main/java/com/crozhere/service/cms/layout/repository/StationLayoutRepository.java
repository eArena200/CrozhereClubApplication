package com.crozhere.service.cms.layout.repository;

import com.crozhere.service.cms.layout.repository.entity.StationLayout;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationLayoutRepository extends MongoRepository<StationLayout, String> {
    boolean existsByStationId(Long stationId);
}

