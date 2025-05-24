package com.crozhere.service.cms.club.service.impl;

import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.repository.dao.StationDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.dao.exception.StationDAOException;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.club.service.ClubAdminService;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubAdminServiceException;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import com.crozhere.service.cms.layout.controller.model.request.AddStationLayoutRequest;
import com.crozhere.service.cms.layout.controller.model.request.CreateClubLayoutRequest;
import com.crozhere.service.cms.layout.controller.model.response.RawClubLayoutResponse;
import com.crozhere.service.cms.layout.controller.model.response.RawStationLayoutResponse;
import com.crozhere.service.cms.layout.service.ClubLayoutService;
import com.crozhere.service.cms.layout.service.exception.ClubLayoutServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class ClubServiceImpl implements ClubService {

    private final ClubDao clubDAO;
    private final StationDao stationDAO;

    private final ClubAdminService clubAdminService;
    private final ClubLayoutService clubLayoutService;

    public ClubServiceImpl(
            @Qualifier("ClubSqlDao") ClubDao clubDAO,
            @Qualifier("StationSqlDao") StationDao stationDAO,
            ClubAdminService clubAdminService,
            ClubLayoutService clubLayoutService){
        this.clubDAO = clubDAO;
        this.stationDAO = stationDAO;
        this.clubAdminService = clubAdminService;
        this.clubLayoutService = clubLayoutService;
    }

    @Override
    public Club createClub(CreateClubRequest createClubRequest)
            throws ClubServiceException {
        try {
            ClubAdmin clubAdmin =
                    clubAdminService.getClubAdminById(createClubRequest.getClubAdminId());

            Club club = Club.builder()
                    .clubAdmin(clubAdmin)
                    .name(createClubRequest.getName())
                    .build();
            clubDAO.save(club);

            RawClubLayoutResponse clubLayoutResponse;
            try {
                clubLayoutResponse = clubLayoutService.createClubLayout(
                        CreateClubLayoutRequest.builder()
                                .clubId(club.getId())
                                .build());
            } catch (ClubLayoutServiceException e) {
                log.error("Exception in creating clubLayout for clubId: {}", club.getId());
                clubDAO.delete(club.getId());
                throw e;
            }

            club.setClubLayoutId(clubLayoutResponse.getId());
            clubDAO.update(club.getId(), club);

            return club;
        } catch (ClubAdminServiceException e){
            log.error("Exception while getting clubAdmin for clubAdminId: {}",
                    createClubRequest.getClubAdminId());
            throw new ClubServiceException("CreateClubException", e);
        } catch (ClubDAOException e){
            log.error("Exception while saving club for clubAdminId: {}",
                    createClubRequest.getClubAdminId());
            throw new ClubServiceException("CreateClubException", e);
        } catch (ClubLayoutServiceException e){
            log.error("Exception in creating club-layout for request: {}", createClubRequest);
            throw new ClubServiceException("CreateClubException", e);
        }
    }

    @Override
    public Club getClubById(Long clubId) throws ClubServiceException {
        try {
            return clubDAO.getById(clubId);
        } catch (DataNotFoundException e) {
            log.error("club not found for clubId: {}", clubId);
            throw new ClubServiceException("GetClubByIdException", e);
        } catch (ClubDAOException e){
            log.error("Exception while getting club for clubId: {}", clubId);
            throw new ClubServiceException("GetClubByIdException", e);
        }
    }

    @Override
    public Club updateClub(Long clubId, UpdateClubRequest updateClubRequest)
            throws ClubServiceException {
        try {
            Club club = getClubById(clubId);
            if(StringUtils.hasText(updateClubRequest.getName())){
                club.setName(updateClubRequest.getName());
            }
            clubDAO.update(clubId, club);
            return club;
        } catch (ClubDAOException e){
            log.error("Exception while updating club for clubId: {}", clubId);
            throw new ClubServiceException("UpdateClubException", e);
        }
    }

    @Override
    public void deleteClub(Long clubId) throws ClubServiceException {
        try {
            Club club = getClubById(clubId);
            List<Long> stationsIds =
                    stationDAO.getStationsByClubId(clubId)
                            .stream()
                            .map(Station::getId)
                            .toList();


            clubLayoutService.deleteClubLayout(club.getClubLayoutId());
            stationDAO.deleteAllById(stationsIds);
            clubDAO.delete(clubId);

        } catch (ClubLayoutServiceException e) {
            log.error("Exception in deleting club-layout for clubId: {}", clubId);
            throw new ClubServiceException("DeleteClubException", e);
        } catch (ClubDAOException e){
            log.error("Exception while deleting club for clubId: {}", clubId);
            throw new ClubServiceException("DeleteClubException", e);
        }
    }

    @Override
    public List<Club> getAllClubs() throws ClubServiceException {
        return List.of();
    }

    @Override
    public List<Club> getClubsByAdmin(Long clubAdminId) throws ClubServiceException {
        try {
            return clubDAO.getByAdmin(clubAdminId);
        } catch (ClubDAOException e){
            log.error("Exception while getting clubs for clubAdminId: {}", clubAdminId);
            throw new ClubServiceException("GetClubsByClubAdminIdException", e);
        }
    }

    @Override
    public Station addStation(AddStationRequest addStationRequest) throws ClubServiceException {
        try {
            Club club = getClubById(addStationRequest.getClubId());
            Station station = Station.builder()
                    .club(club)
                    .stationName(addStationRequest.getStationName())
                    .stationType(addStationRequest.getStationType())
                    .stationGroupLayoutId(addStationRequest.getStationGroupLayoutId())
                    .isActive(true)
                    .build();

            stationDAO.save(station);

            RawStationLayoutResponse rawStationLayoutResponse;
            try {
                rawStationLayoutResponse = clubLayoutService.addStationLayout(
                        AddStationLayoutRequest.builder()
                                .stationGroupLayoutId(
                                        addStationRequest.getStationGroupLayoutId())
                                .stationType(addStationRequest.getStationType())
                                .stationId(station.getId())
                                .offsetX(1)
                                .offsetY(1)
                                .height(10)
                                .width(10)
                                .build());
            } catch (ClubLayoutServiceException e) {
                log.error("Exception in adding station-layout for request: {}", addStationRequest);
                stationDAO.delete(station.getId());
                throw new ClubServiceException("AddStationException", e);
            }

            station.setStationLayoutId(rawStationLayoutResponse.getId());
            stationDAO.update(station.getId(), station);

            return station;
        } catch (StationDAOException e) {
            log.error("Exception while saving station for clubId: {}",
                    addStationRequest.getClubId());
            throw new ClubServiceException("AddStationException", e);
        }
    }

    @Override
    public Station updateStation(Long stationId, UpdateStationRequest updateStationRequest)
            throws ClubServiceException {
        try {
            Station station = getStation(stationId);
            if(StringUtils.hasText(updateStationRequest.getStationName())){
                station.setStationName(updateStationRequest.getStationName());
            }

            stationDAO.update(stationId, station);
            return station;
        } catch (StationDAOException e){
            log.error("Exception while updating station for stationId: {}", stationId);
            throw new ClubServiceException("UpdateStationException", e);
        }
    }

    @Override
    public Station getStation(Long stationId) throws ClubServiceException {
        try {
            return stationDAO.getById(stationId);
        } catch (DataNotFoundException e){
            log.error("station not found for stationId: {}", stationId);
            throw new ClubServiceException("GetStationException", e);
        } catch (StationDAOException e){
            log.error("Exception while getting station for stationId: {}", stationId);
            throw new ClubServiceException("GetStationException", e);
        }
    }

    @Override
    public void deleteStation(Long stationId) throws ClubServiceException {
        try {
            Station station = stationDAO.getById(stationId);
            clubLayoutService.deleteStationLayout(station.getStationLayoutId());
            stationDAO.delete(stationId);
        } catch (ClubLayoutServiceException e) {
            log.error("Exception in deleting station-layout for stationId: {}", stationId);
            throw new ClubServiceException("DeleteStationException", e);
        } catch (StationDAOException e){
            log.error("Exception while deleting station for stationId: {}", stationId);
            throw new ClubServiceException("DeleteStationException", e);
        }
    }

    @Override
    public List<Station> getStationsByClubId(Long clubId) throws ClubServiceException {
        try {
            return stationDAO.getStationsByClubId(clubId);
        } catch (StationDAOException e){
            log.error("Exception while getting stations for clubId: {}", clubId);
            throw new ClubServiceException("GetStationByClubIdException", e);
        }
    }

    @Override
    public List<Station> getStationsByClubIdAndType(Long clubId, StationType stationType)
            throws ClubServiceException {
        try {
            return getStationsByClubId(clubId).stream()
                    .filter(station ->
                            station.getStationType().equals(stationType))
                    .toList();
        } catch (Exception e){
            log.error("Exception while getting stations for clubId {} and type {}", clubId, stationType);
            throw new ClubServiceException("GetStationsByClubIdAndType", e);
        }
    }

}
