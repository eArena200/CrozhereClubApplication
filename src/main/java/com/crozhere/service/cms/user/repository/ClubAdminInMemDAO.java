package com.crozhere.service.cms.user.repository;

import com.crozhere.service.cms.user.repository.exception.ClubAdminDAOException;
import com.crozhere.service.cms.user.repository.model.ClubAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("ClubAdminInMemDAO")
public class ClubAdminInMemDAO implements ClubAdminDAO {

    private final Map<String, ClubAdmin> clubAdminStore;

    public ClubAdminInMemDAO(){
        this.clubAdminStore = new HashMap<>();
    }

    @Override
    public void save(ClubAdmin clubAdmin) throws ClubAdminDAOException {
        if(clubAdminStore.containsKey(clubAdmin.getId())){
            log.info("ClubAdminId {} already exists", clubAdmin.getId());
            throw new ClubAdminDAOException("SaveException");
        }

        clubAdminStore.putIfAbsent(clubAdmin.getId(), clubAdmin);
    }

    @Override
    public ClubAdmin get(String clubAdminId) throws ClubAdminDAOException {
        if(clubAdminStore.containsKey(clubAdminId)){
            return clubAdminStore.get(clubAdminId);
        } else {
            log.info("ClubAdminId {} doesn't exist", clubAdminId);
            throw new ClubAdminDAOException("ReadException");
        }
    }

    @Override
    public void update(String clubAdminId, ClubAdmin clubAdmin) throws ClubAdminDAOException {
        if(clubAdminStore.containsKey(clubAdminId)){
            clubAdminStore.put(clubAdminId, clubAdmin);
        } else {
            log.info("ClubAdminId {} doesn't exist for update", clubAdminId);
            throw new ClubAdminDAOException("UpdateException");
        }
    }

    @Override
    public void delete(String clubAdminId) throws ClubAdminDAOException {
        if(clubAdminStore.containsKey(clubAdminId)){
            clubAdminStore.remove(clubAdminId);
        } else {
            log.info("StationId {} doesn't exist for delete", clubAdminId);
            throw new ClubAdminDAOException("DeleteException");
        }
    }
}
