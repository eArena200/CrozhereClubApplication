package com.crozhere.service.cms.club.repository.dao;

import com.crozhere.service.cms.club.repository.dao.exception.ClubAdminDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;

import java.util.Optional;

public interface ClubAdminDao {

    void save(ClubAdmin clubAdmin) throws ClubAdminDAOException;

    void update(Long id, ClubAdmin updated) throws ClubAdminDAOException;

    ClubAdmin getById(Long id) throws ClubAdminDAOException, DataNotFoundException;
    Optional<ClubAdmin> findById(Long id) throws ClubAdminDAOException;

    void deleteById(Long id) throws ClubAdminDAOException;

    Optional<ClubAdmin> findByUserId(Long userId) throws ClubAdminDAOException;
}
