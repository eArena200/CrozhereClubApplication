package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;

import java.util.List;

public interface ClubService {
    Club createClub(CreateClubRequest createClubRequest)
            throws ClubServiceException;
    Club getClubById(String clubId) throws ClubServiceException;
    Club updateClub(String clubId, UpdateClubRequest updateClubRequest)
            throws ClubServiceException;
    void deleteClub(String clubId) throws ClubServiceException;

    List<Club> getAllClubs() throws ClubServiceException;
    List<Club> getClubsByAdmin(String clubAdminId) throws ClubServiceException;

    Station addStation(AddStationRequest addStationRequest)
            throws ClubServiceException;
    Station updateStation(String stationId, UpdateStationRequest updateStationRequest)
            throws ClubServiceException;
    Station getStation(String stationId) throws ClubServiceException;
    void deleteStation(String stationId) throws ClubServiceException;

    List<Station> getStationsByClubId(String clubId) throws ClubServiceException;
}
