package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.exception.StationDAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("StationInMemDAO")
public class StationInMemDAO implements StationDAO {

    private final Map<String, Station> stationStore;

    public StationInMemDAO(){
        this.stationStore = new HashMap<>();
    }

    @Override
    public void save(Station station) throws StationDAOException {
        if(stationStore.containsKey(station.getStationId())){
            log.info("StationId {} already exists", station.getStationId());
            throw new StationDAOException("SaveException");
        }

        stationStore.putIfAbsent(station.getStationId(), station);
    }

    @Override
    public Station get(String stationId) throws StationDAOException {
        if(stationStore.containsKey(stationId)){
            return stationStore.get(stationId);
        } else {
            log.info("StationId {} doesn't exist", stationId);
            throw new StationDAOException("ReadException");
        }
    }

    @Override
    public void update(String stationId, Station station) throws StationDAOException {
        if(stationStore.containsKey(stationId)){
            stationStore.put(stationId, station);
        } else {
            log.info("StationId {} doesn't exist for update", stationId);
            throw new StationDAOException("UpdateException");
        }
    }

    @Override
    public void delete(String stationId) throws StationDAOException {
        if(stationStore.containsKey(stationId)){
            stationStore.remove(stationId);
        } else {
            log.info("StationId {} doesn't exist for delete", stationId);
            throw new StationDAOException("DeleteException");
        }
    }

    @Override
    public List<Station> getStationsByClubId(String clubId) throws StationDAOException {
        try {
            return stationStore.values().stream()
                    .filter(station -> station.getClubId().equals(clubId))
                    .collect(Collectors.toList());
        } catch (Exception e){
            log.info("Exception occurred while getting stations for clubId: {}", clubId);
            throw new StationDAOException("GetStationsByClubException");
        }
    }
}
