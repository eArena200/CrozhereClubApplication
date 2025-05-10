package com.crozhere.service.cms.club.repository;

import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.exception.ClubDAOException;

import java.util.List;

public interface ClubDAO {
    void save(Club club) throws ClubDAOException;
    Club get(String clubId) throws ClubDAOException;
    void update(String clubId, Club club) throws ClubDAOException;
    void delete(String clubId) throws ClubDAOException;

    List<Club> getAll() throws ClubDAOException;
    List<Club> getByAdmin(String clubAdminId) throws ClubDAOException;
}
