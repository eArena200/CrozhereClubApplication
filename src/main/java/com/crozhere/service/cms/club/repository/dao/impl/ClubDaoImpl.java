package com.crozhere.service.cms.club.repository.dao.impl;

import com.crozhere.service.cms.club.repository.ClubRepository;
import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.Club;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component("ClubSqlDao")
public class ClubDaoImpl implements ClubDao {

    private final ClubRepository clubRepository;

    @Autowired
    public ClubDaoImpl(ClubRepository clubRepository){
        this.clubRepository = clubRepository;
    }

    @Override
    public void save(Club club) throws ClubDAOException {
        try {
            clubRepository.save(club);
        } catch (Exception e) {
            log.error("Failed to save club", e);
            throw new ClubDAOException("Error saving club", e);
        }
    }

    @Override
    public Optional<Club> findById(Long clubId) throws ClubDAOException {
        try {
            return clubRepository.findById(clubId);
        } catch (Exception e) {
            log.error("Failed to find club by ID: {}", clubId, e);
            throw new ClubDAOException("Error finding club", e);
        }
    }

    @Override
    public Club getById(Long clubId) throws DataNotFoundException, ClubDAOException {
        try {
            return clubRepository.findById(clubId)
                    .orElseThrow(() -> new DataNotFoundException("Club not found with ID: " + clubId));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get club by ID: {}", clubId, e);
            throw new ClubDAOException("Error getting club", e);
        }
    }

    @Override
    public void update(Long clubId, Club updatedClub) throws ClubDAOException {
        try {
            if (!clubRepository.existsById(clubId)) {
                throw new ClubDAOException("Club not found with ID: " + clubId);
            }
            updatedClub.setId(clubId);
            clubRepository.save(updatedClub);
        } catch (Exception e) {
            log.error("Failed to update club: {}", clubId, e);
            throw new ClubDAOException("Error updating club", e);
        }
    }

    @Override
    public void delete(Long clubId) throws ClubDAOException {
        try {
            clubRepository.deleteById(clubId);
        } catch (Exception e) {
            log.error("Failed to delete club: {}", clubId, e);
            throw new ClubDAOException("Error deleting club", e);
        }
    }

    @Override
    public List<Club> getAll() throws ClubDAOException {
        try {
            return clubRepository.findAll();
        } catch (Exception e) {
            log.error("Failed to retrieve all clubs", e);
            throw new ClubDAOException("Error getting all clubs", e);
        }
    }

    @Override
    public List<Club> getByAdmin(Long clubAdminId) throws ClubDAOException {
        try {
            return clubRepository.findByClubAdmin_Id(clubAdminId);
        } catch (Exception e) {
            log.error("Failed to get clubs for clubAdminId: {}", clubAdminId, e);
            throw new ClubDAOException("Error getting clubs by admin", e);
        }
    }
}
