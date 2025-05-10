package com.crozhere.service.cms.club.service;


import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.repository.StationDAO;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.ClubDAO;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.exception.StationDAOException;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ClubServiceImpl implements ClubService {
    private final ClubDAO clubDAO;
    private final StationDAO stationDAO;

    public ClubServiceImpl(
            @Qualifier("ClubInMemDAO") ClubDAO clubDAO,
            @Qualifier("StationInMemDAO") StationDAO stationDAO){
        this.clubDAO = clubDAO;
        this.stationDAO = stationDAO;
    }


    @Override
    public Club createClub(CreateClubRequest createClubRequest)
            throws ClubServiceException {
        Club club = Club.builder()
                .clubId(UUID.randomUUID().toString())
                .clubAdminId(createClubRequest.getClubAdminId())
                .name(createClubRequest.getName())
                .build();

        try {
            clubDAO.save(club);
            return club;
        } catch (ClubDAOException clubDAOException){
            log.error("Exception while saving clubId: {}", club.getClubId());
            throw new ClubServiceException("CreateClubException");
        }
    }

    @Override
    public Club getClubById(String clubId) throws ClubServiceException {
        try {
            return clubDAO.get(clubId);
        } catch (ClubDAOException clubDAOException){
            log.error("Exception while getting clubId: {}", clubId);
            throw new ClubServiceException("GetClubByIdException");
        }
    }

    @Override
    public Club updateClub(String clubId, UpdateClubRequest updateClubRequest)
            throws ClubServiceException {
        try {
            Club club = clubDAO.get(clubId);
            club.setName(updateClubRequest.getName());
            clubDAO.update(clubId, club);
            return club;
        } catch (ClubDAOException clubDAOException){
            log.error("Exception while updating clubId: {}", clubId);
            throw new ClubServiceException("UpdateClubException");
        }
    }

    @Override
    public void deleteClub(String clubId) throws ClubServiceException {
        try {
            clubDAO.delete(clubId);
        } catch (ClubDAOException clubDAOException){
            log.error("Exception while deleting clubId: {}", clubId);
            throw new ClubServiceException("DeleteClubException");
        }
    }

    @Override
    public List<Club> getAllClubs() throws ClubServiceException {
        try {
            return clubDAO.getAll();
        } catch (ClubDAOException clubDAOException){
            log.error("Exception while getting all clubs");
            throw new ClubServiceException("GetAllClubsException");
        }
    }

    @Override
    public List<Club> getClubsByAdmin(String clubAdminId)
            throws ClubServiceException {
        try {
            return clubDAO.getByAdmin(clubAdminId);
        } catch (ClubDAOException clubDAOException) {
            log.error("Exception while getting clubs for clubAdminId: {}", clubAdminId);
            throw new ClubServiceException("GetClubsByAdminException");
        }
    }

    @Override
    public Station addStation(AddStationRequest addStationRequest)
            throws ClubServiceException {
        Club club = getClubById(addStationRequest.getClubId());
        Station station = Station.builder()
                .stationId(UUID.randomUUID().toString())
                .clubId(club.getClubId())
                .stationName(addStationRequest.getStationName())
                .stationType(addStationRequest.getStationType())
                .isAvailable(true)
                .build();
        try {
            stationDAO.save(station);
            return station;
        } catch (StationDAOException stationDAOException){
            log.error("Exception while saving Station: {} ", station.toString());
            throw new ClubServiceException("AddStationException");
        }
    }

    @Override
    public Station updateStation(String stationId,
                                 UpdateStationRequest updateStationRequest)
            throws ClubServiceException {
        try {
            Station station = stationDAO.get(stationId);
            station.setStationName(updateStationRequest.getStationName());
            stationDAO.update(stationId, station);
            return station;
        } catch (StationDAOException stationDAOException){
            log.error("Exception while updating Station: {}", stationId);
            throw new ClubServiceException("UpdateStationException");
        }
    }

    @Override
    public Station getStation(String stationId) throws ClubServiceException {
        try {
            return stationDAO.get(stationId);
        } catch (StationDAOException stationDAOException){
            log.error("Exception while getting Station: {}", stationId);
            throw new ClubServiceException("GetStationException");
        }
    }

    @Override
    public void deleteStation(String stationId) throws ClubServiceException {
        try {
            stationDAO.delete(stationId);
        } catch (StationDAOException stationDAOException){
            log.error("Exception while deleting Station: {}", stationId);
            throw new ClubServiceException("DeleteStationException");
        }
    }

    @Override
    public List<Station> getStationsByClubId(String clubId) throws ClubServiceException {
        try {
            return stationDAO.getStationsByClubId(clubId);
        } catch (StationDAOException stationDAOException){
            log.error("Exception while getting clubs for clubId: {}", clubId);
            throw new ClubServiceException("GetStationsByClubId");
        }
    }
}
