package com.crozhere.service.cms.club.repository.dao.impl;

import com.crozhere.service.cms.club.repository.dao.StationDao;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.dao.exception.StationDAOException;
import com.crozhere.service.cms.club.repository.entity.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("StationInMemDao")
public class StationInMemDao implements StationDao {

    private final Map<Long, Station> stationStore;

    public StationInMemDao() {
        this.stationStore = new HashMap<>();
    }

    @Override
    public void save(Station station) throws StationDAOException {
        if (station.getId() == null) {
            throw new StationDAOException("Station ID cannot be null");
        }
        stationStore.put(station.getId(), station);
    }

    @Override
    public Optional<Station> findById(Long stationId) throws StationDAOException {
        return Optional.ofNullable(stationStore.get(stationId));
    }

    @Override
    public Station getById(Long stationId) throws DataNotFoundException, StationDAOException {
        Station station = stationStore.get(stationId);
        if (station == null) {
            throw new StationDAOException("Station not found with ID: " + stationId);
        }
        return station;
    }

    @Override
    public void update(Long stationId, Station station) throws StationDAOException {
        if (!stationStore.containsKey(stationId)) {
            throw new StationDAOException("Cannot update. Station not found with ID: " + stationId);
        }
        station.setId(stationId);
        stationStore.put(stationId, station);
    }

    @Override
    public void delete(Long stationId) throws StationDAOException {
        if (!stationStore.containsKey(stationId)) {
            throw new StationDAOException("Cannot delete. Station not found with ID: " + stationId);
        }
        stationStore.remove(stationId);
    }

    @Override
    public List<Station> getStationsByClubId(Long clubId) throws StationDAOException {
        return stationStore.values().stream()
                .filter(station -> station.getClub() != null && Objects.equals(station.getClub().getId(), clubId))
                .collect(Collectors.toList());
    }
}
