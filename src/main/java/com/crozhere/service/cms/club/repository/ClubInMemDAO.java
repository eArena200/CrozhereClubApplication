package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.exception.ClubDAOException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMem")
public class ClubInMemDAO implements ClubDAO {

    private final Map<String, Club> clubStore;

    public ClubInMemDAO(){
        this.clubStore = new HashMap<>();
    }

    @Override
    public void save(Club club) throws ClubDAOException {
        if(clubStore.containsKey(club.getId())){
            throw new ClubDAOException("Duplicate clubId");
        }

        clubStore.putIfAbsent(club.getId(), club);
    }

    @Override
    public Club get(String clubId) throws ClubDAOException {
        if(clubStore.containsKey(clubId)){
            return clubStore.get(clubId);
        }

        throw new ClubDAOException("ClubId doesn't exist");
    }

    @Override
    public void update(String clubId, Club club) throws ClubDAOException {
        if(clubStore.containsKey(clubId)){
            clubStore.put(clubId, club);
        } else {
            throw new ClubDAOException("ClubId doesn't exist");
        }
    }

    @Override
    public void delete(String clubId) throws ClubDAOException {
        if(clubStore.containsKey(clubId)){
            clubStore.remove(clubId);
        } else {
            throw new ClubDAOException("ClubId doesn't exist");
        }
    }

    @Override
    public List<Club> getByAdmin(String clubAdminId) throws ClubDAOException {
        try {
            return clubStore.values().stream()
                    .filter(club -> club.getClubAdminId().equals(clubAdminId))
                    .collect(Collectors.toList());
        } catch (Exception e){
            throw new ClubDAOException("Exception occurred while getting the club list by adminId");
        }
    }
}
