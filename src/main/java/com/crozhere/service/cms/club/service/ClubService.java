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
    Club createClub(CreateClubRequest createClubRequest)
            throws ClubServiceException;
    Club getClubById(Long clubId) throws ClubServiceException;
    Club updateClub(Long clubId, UpdateClubRequest updateClubRequest)
            throws ClubServiceException;
    void deleteClub(Long clubId) throws ClubServiceException;

    List<Club> getAllClubs() throws ClubServiceException;

    List<Club> getClubsByAdmin(Long clubAdminId) throws ClubServiceException;

    Station addStation(AddStationRequest addStationRequest)
            throws ClubServiceException;
    Station updateStation(Long stationId, UpdateStationRequest updateStationRequest)
            throws ClubServiceException;
    Station getStation(Long stationId) throws ClubServiceException;
    void deleteStation(Long stationId) throws ClubServiceException;

    List<Station> getStationsByClubId(Long clubId) throws ClubServiceException;
    List<Station> getStationsByClubIdAndType(Long clubId, StationType stationType)
            throws ClubServiceException;
}
