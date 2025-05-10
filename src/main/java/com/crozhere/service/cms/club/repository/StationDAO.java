package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.exception.StationDAOException;

import java.util.List;

public interface StationDAO {
    void save(Station station) throws StationDAOException;
    Station get(String stationId) throws StationDAOException;
    void update(String stationId, Station station) throws StationDAOException;
    void delete(String stationId) throws StationDAOException;

    List<Station> getStationsByClubId(String clubId) throws StationDAOException;
}
