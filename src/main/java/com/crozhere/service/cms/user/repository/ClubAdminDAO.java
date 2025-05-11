package com.crozhere.service.cms.user.repository;

import com.crozhere.service.cms.user.repository.exception.ClubAdminDAOException;
import com.crozhere.service.cms.user.repository.model.ClubAdmin;

public interface ClubAdminDAO {
    void save(ClubAdmin clubAdmin) throws ClubAdminDAOException;
    ClubAdmin get(String clubAdminId) throws ClubAdminDAOException;
    void update(String clubAdminId, ClubAdmin clubAdmin) throws ClubAdminDAOException;
    void delete(String clubAdminId) throws ClubAdminDAOException;
}
