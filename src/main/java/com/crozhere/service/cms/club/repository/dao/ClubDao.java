package com.crozhere.service.cms.club.repository.dao;

import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;

import java.util.List;
import java.util.Optional;

public interface ClubDao {
    void save(Club club) throws ClubDAOException;

    Optional<Club> findById(Long clubId) throws ClubDAOException;
    Club getById(Long clubId) throws DataNotFoundException, ClubDAOException;

    List<Club> getClubsByIds(List<Long> clubIds) throws ClubDAOException;

    void update(Long clubId, Club club) throws ClubDAOException;

    void delete(Long clubId) throws ClubDAOException;

    List<Club> getAll() throws ClubDAOException;

    List<Club> getByAdmin(Long clubAdminId) throws ClubDAOException;
}
