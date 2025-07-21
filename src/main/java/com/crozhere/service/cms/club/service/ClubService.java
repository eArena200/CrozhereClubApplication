package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;

import java.util.List;

public interface ClubService {
    // CLUB LEVEL METHODS
    Club createClub(Long clubAdminId, CreateClubRequest createClubRequest)
            throws ClubServiceException;
    List<Club> getClubsByAdmin(Long clubAdminId) throws ClubServiceException;
    Club updateClub(Long clubAdminId, Long clubId, UpdateClubRequest updateClubRequest)
            throws ClubServiceException;
    void deleteClub(Long clubAdminId, Long clubId) throws ClubServiceException;

    Club getClubById(Long clubId) throws ClubServiceException;
    List<Club> getClubsByIds(List<Long> clubIds) throws ClubServiceException;
    List<Club> getAllClubs() throws ClubServiceException;


    // STATION LEVEL METHODS
    Station addStation(Long clubAdminId, AddStationRequest addStationRequest)
            throws ClubServiceException;
    Station updateStation(Long clubAdminId, Long stationId, UpdateStationRequest updateStationRequest)
            throws ClubServiceException;
    Station toggleStationStatus(Long clubAdminId, Long stationId) throws ClubServiceException;
    void deleteStation(Long clubAdminId, Long stationId) throws ClubServiceException;

    Station getStationById(Long stationId) throws ClubServiceException;
    List<Station> getStationsByClubId(Long clubId) throws ClubServiceException;
    List<Station> getStationsByClubIds(List<Long> clubIds) throws ClubServiceException;
    List<Station> getStationsByClubIdAndType(Long clubId, StationType stationType)
            throws ClubServiceException;
}
