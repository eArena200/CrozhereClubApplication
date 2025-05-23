package com.crozhere.service.cms.club.repository.dao.impl;

import com.crozhere.service.cms.club.repository.StationRepository;
import com.crozhere.service.cms.club.repository.dao.StationDao;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.dao.exception.StationDAOException;
import com.crozhere.service.cms.club.repository.entity.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component("StationSqlDao")
public class StationSqlDao implements StationDao {

    private final StationRepository stationRepository;

    @Autowired
    public StationSqlDao(StationRepository stationRepository){
        this.stationRepository = stationRepository;
    }

    @Override
    public void save(Station station) throws StationDAOException {
        try {
            stationRepository.save(station);
        } catch (Exception e) {
            log.error("Failed to save station: {}", station, e);
            throw new StationDAOException("Error saving station", e);
        }
    }

    @Override
    public Optional<Station> findById(Long stationId) throws StationDAOException {
        try {
            return stationRepository.findById(stationId);
        } catch (Exception e) {
            log.error("Failed to find station with ID: {}", stationId, e);
            throw new StationDAOException("Error finding station", e);
        }
    }

    @Override
    public Station getById(Long stationId) throws DataNotFoundException, StationDAOException {
        try {
            return stationRepository.findById(stationId)
                    .orElseThrow(() -> new DataNotFoundException("Station not found with ID: " + stationId));
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e) {
            log.error("Failed to get station with ID: {}", stationId, e);
            throw new StationDAOException("Error getting station", e);
        }
    }

    @Override
    public void update(Long stationId, Station station) throws StationDAOException {
        try {
            if (!stationRepository.existsById(stationId)) {
                throw new StationDAOException("Station not found with ID: " + stationId);
            }
            station.setId(stationId);
            stationRepository.save(station);
        } catch (Exception e) {
            log.error("Failed to update station with ID: {}", stationId, e);
            throw new StationDAOException("Error updating station", e);
        }
    }

    @Override
    public void delete(Long stationId) throws StationDAOException {
        try {
            stationRepository.deleteById(stationId);
        } catch (Exception e) {
            log.error("Failed to delete station with ID: {}", stationId, e);
            throw new StationDAOException("Error deleting station", e);
        }
    }

    @Override
    public void deleteAllById(List<Long> stationIds) throws StationDAOException {
        try {
            stationRepository.deleteAllById(stationIds);
        } catch (Exception e){
            log.error("Failed to delete station with IDs: {}", stationIds, e);
            throw new StationDAOException("Error deleting station", e);
        }
    }

    @Override
    public List<Station> getStationsByClubId(Long clubId) throws StationDAOException {
        try {
            return stationRepository.findByClub_Id(clubId);
        } catch (Exception e) {
            log.error("Failed to fetch stations for club ID: {}", clubId, e);
            throw new StationDAOException("Error fetching stations by club ID", e);
        }
    }
}
