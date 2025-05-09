package com.crozhere.service.cms.club.service;


import com.crozhere.service.cms.club.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.repository.Club;
import com.crozhere.service.cms.club.repository.ClubDAO;
import com.crozhere.service.cms.club.repository.exception.ClubDAOException;
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

    public ClubServiceImpl(
            @Qualifier("InMem") ClubDAO clubDAO){
        this.clubDAO = clubDAO;
    }


    @Override
    public Club createClub(CreateClubRequest createClubRequest)
            throws ClubServiceException {
        Club club = Club.builder()
                .id(UUID.randomUUID().toString())
                .clubAdminId(createClubRequest.getClubAdminId())
                .name(createClubRequest.getName())
                .build();

        try {
            clubDAO.save(club);
            return club;
        } catch (ClubDAOException clubDAOException){
            log.error("Exception while saving clubId: {}", club.getId());
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
            Club existingClub = clubDAO.get(clubId);
            Club updatedClub = Club.builder()
                    .id(clubId)
                    .clubAdminId(existingClub.getClubAdminId())
                    .name(updateClubRequest.getName())
                    .build();
            clubDAO.update(clubId, updatedClub);
            return updatedClub;
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
    public List<Club> getClubsByAdmin(String clubAdminId)
            throws ClubServiceException {
        try {
            return clubDAO.getByAdmin(clubAdminId);
        } catch (ClubDAOException clubDAOException) {
            log.error("Exception while getting clubs for clubAdminId: {}", clubAdminId);
            throw new ClubServiceException("GetClubsByAdminException");
        }
    }
}
