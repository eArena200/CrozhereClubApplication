package com.crozhere.service.cms.club.repository.dao.impl;

import com.crozhere.service.cms.club.repository.dao.ClubAdminDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubAdminDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("ClubAdminInMemDao")
public class ClubAdminInMemDao implements ClubAdminDao {

    private final Map<Long, ClubAdmin> store = new HashMap<>();

    @Override
    public void save(ClubAdmin clubAdmin) throws ClubAdminDAOException {
        if (store.containsKey(clubAdmin.getId())) {
            throw new ClubAdminDAOException("ClubAdmin already exists with ID: " + clubAdmin.getId());
        }
        store.put(clubAdmin.getId(), clubAdmin);
    }

    @Override
    public ClubAdmin getById(Long id) throws DataNotFoundException {
        ClubAdmin admin = store.get(id);
        if (admin == null) {
            throw new DataNotFoundException("ClubAdmin not found with ID: " + id);
        }
        return admin;
    }

    @Override
    public Optional<ClubAdmin> findById(Long id) throws ClubAdminDAOException {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void update(Long id, ClubAdmin updated) throws ClubAdminDAOException {
        if (!store.containsKey(id)) {
            throw new ClubAdminDAOException("ClubAdmin not found with ID: " + id);
        }
        updated.setId(id);
        store.put(id, updated);
    }

    @Override
    public void deleteById(Long id) throws ClubAdminDAOException {
        store.remove(id);
    }

    @Override
    public Optional<ClubAdmin> findByUserId(Long userId) {
        return store.values().stream()
                .filter(admin -> admin.getUser().getId().equals(userId))
                .findFirst();
    }
}
