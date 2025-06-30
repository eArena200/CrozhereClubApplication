package com.crozhere.service.cms.user.repository.dao.impl;

import com.crozhere.service.cms.user.repository.ClubAdminRepository;
import com.crozhere.service.cms.user.repository.dao.ClubAdminDao;
import com.crozhere.service.cms.user.repository.dao.exception.ClubAdminDAOException;
import com.crozhere.service.cms.user.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ClubAdminDaoImpl implements ClubAdminDao {

    private final ClubAdminRepository clubAdminRepository;

    @Override
    public void save(ClubAdmin clubAdmin) throws ClubAdminDAOException {
        try {
            clubAdminRepository.save(clubAdmin);
        } catch (Exception e) {
            log.error("Failed to create club admin", e);
            throw new ClubAdminDAOException("Error creating club admin", e);
        }
    }

    @Override
    public void update(Long id, ClubAdmin updated) throws ClubAdminDAOException {
        try {
            if (!clubAdminRepository.existsById(id)) {
                throw new ClubAdminDAOException("ClubAdmin not found with ID: " + id);
            }
            updated.setId(id);
            clubAdminRepository.save(updated);
        } catch (Exception e) {
            log.error("Failed to update club admin", e);
            throw new ClubAdminDAOException("Error updating club admin", e);
        }
    }

    @Override
    public ClubAdmin getById(Long id) throws DataNotFoundException, ClubAdminDAOException {
        try {
            return clubAdminRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("ClubAdmin not found with ID: " + id));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ClubAdminDAOException("Error in getting clubAdmin by ID", e);
        }
    }

    @Override
    public Optional<ClubAdmin> findById(Long id) {
        return clubAdminRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) throws ClubAdminDAOException {
        try {
            clubAdminRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete club admin", e);
            throw new ClubAdminDAOException("Error deleting club admin", e);
        }
    }

    @Override
    public Optional<ClubAdmin> findByUserId(Long userId) {
        return clubAdminRepository.findByUser_Id(userId);
    }

}
