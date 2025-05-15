package com.crozhere.service.cms.club.service.impl;

import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.repository.dao.StationDao;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClubServiceImpl implements ClubService {
    private final ClubDao clubDAO;
    private final StationDao stationDAO;

    public ClubServiceImpl(
            @Qualifier("ClubSqlDao") ClubDao clubDAO,
            @Qualifier("StationSqlDao") StationDao stationDAO){
        this.clubDAO = clubDAO;
        this.stationDAO = stationDAO;
    }

    @Override
    public Club createClub(CreateClubRequest createClubRequest) throws ClubServiceException {
        return null;
    }

    @Override
    public Club getClubById(Long clubId) throws ClubServiceException {
        return null;
    }

    @Override
    public Club updateClub(Long clubId, UpdateClubRequest updateClubRequest) throws ClubServiceException {
        return null;
    }

    @Override
    public void deleteClub(Long clubId) throws ClubServiceException {

    }

    @Override
    public List<Club> getAllClubs() throws ClubServiceException {
        return List.of();
    }

    @Override
    public List<Club> getClubsByAdmin(Long clubAdminId) throws ClubServiceException {
        return List.of();
    }

    @Override
    public Station addStation(AddStationRequest addStationRequest) throws ClubServiceException {
        return null;
    }

    @Override
    public Station updateStation(Long stationId, UpdateStationRequest updateStationRequest) throws ClubServiceException {
        return null;
    }

    @Override
    public Station getStation(Long stationId) throws ClubServiceException {
        return null;
    }

    @Override
    public void deleteStation(Long stationId) throws ClubServiceException {

    }

    @Override
    public List<Station> getStationsByClubId(Long clubId) throws ClubServiceException {
        return List.of();
    }

}
