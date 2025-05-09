package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.club.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.repository.Club;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;

import java.util.List;

public interface ClubService {
    Club createClub(CreateClubRequest createClubRequest) throws ClubServiceException;
    Club getClubById(String clubId) throws ClubServiceException;
    Club updateClub(String clubId, UpdateClubRequest updateClubRequest) throws ClubServiceException;
    void deleteClub(String clubId) throws ClubServiceException;

    List<Club> getClubsByAdmin(String clubAdminId) throws ClubServiceException;
}
