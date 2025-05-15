package com.crozhere.service.cms.club.repository.dao.impl;

import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.Club;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("ClubInMemDao")
public class ClubInMemDao implements ClubDao {

    private final Map<Long, Club> clubStore = new HashMap<>();

    @Override
    public void save(Club club) throws ClubDAOException {
        if (club.getId() == null) {
            throw new ClubDAOException("Club ID cannot be null");
        }
        clubStore.put(club.getId(), club);
    }

    @Override
    public Optional<Club> findById(Long clubId) throws ClubDAOException {
        return Optional.ofNullable(clubStore.get(clubId));
    }

    @Override
    public Club getById(Long clubId) throws DataNotFoundException, ClubDAOException {
        Club club = clubStore.get(clubId);
        if (club == null) {
            throw new ClubDAOException("Club not found with ID: " + clubId);
        }
        return club;
    }

    @Override
    public void update(Long clubId, Club updatedClub) throws ClubDAOException {
        if (!clubStore.containsKey(clubId)) {
            throw new ClubDAOException("Cannot update. Club not found with ID: " + clubId);
        }
        updatedClub.setId(clubId);
        clubStore.put(clubId, updatedClub);
    }

    @Override
    public void delete(Long clubId) throws ClubDAOException {
        if (!clubStore.containsKey(clubId)) {
            throw new ClubDAOException("Cannot delete. Club not found with ID: " + clubId);
        }
        clubStore.remove(clubId);
    }

    @Override
    public List<Club> getAll() throws ClubDAOException {
        return new ArrayList<>(clubStore.values());
    }

    @Override
    public List<Club> getByAdmin(Long clubAdminId) throws ClubDAOException {
        return clubStore.values().stream()
                .filter(club -> club.getClubAdmin() != null &&
                        club.getClubAdmin().getId().equals(clubAdminId))
                .collect(Collectors.toList());
    }
}
