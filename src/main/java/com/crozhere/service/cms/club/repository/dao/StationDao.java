package com.crozhere.service.cms.club.repository.dao;

import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.dao.exception.StationDAOException;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    void save(Station station) throws StationDAOException;

    Optional<Station> findById(Long stationId) throws StationDAOException;
    Station getById(Long stationId) throws DataNotFoundException, StationDAOException;

    void update(Long stationId, Station station) throws StationDAOException;

    void delete(Long stationId) throws StationDAOException;
    void deleteAllById(List<Long> stationIds) throws StationDAOException;

    List<Station> getStationsByClubId(Long clubId) throws StationDAOException;
}
