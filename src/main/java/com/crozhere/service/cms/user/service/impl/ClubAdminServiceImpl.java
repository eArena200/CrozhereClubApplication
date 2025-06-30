package com.crozhere.service.cms.user.service.impl;

import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.controller.model.request.UpdateClubAdminRequest;
import com.crozhere.service.cms.user.repository.dao.ClubAdminDao;
import com.crozhere.service.cms.user.repository.dao.exception.ClubAdminDAOException;
import com.crozhere.service.cms.user.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import com.crozhere.service.cms.user.service.ClubAdminService;
import com.crozhere.service.cms.user.service.exception.ClubAdminServiceException;
import com.crozhere.service.cms.user.service.exception.ClubAdminServiceExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubAdminServiceImpl implements ClubAdminService {

    private final ClubAdminDao clubAdminDao;

    @Override
    public ClubAdmin createClubAdminForUser(User user) throws ClubAdminServiceException {
        try {
            ClubAdmin clubAdmin = ClubAdmin.builder()
                    .user(user)
                    .name("ClubAdmin@" + user.getPhone())
                    .phone(user.getPhone())
                    .email("clubadmin@crozhere.com")
                    .build();
            clubAdminDao.save(clubAdmin);
            return clubAdmin;
        } catch (ClubAdminDAOException e){
            log.error("Exception while saving newly created ClubAdmin for userId: {}",
                    user.getId(), e);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.CREATE_CLUB_ADMIN_FAILED);
        }
    }

    @Override
    public ClubAdmin getClubAdminByUserId(Long userId) throws ClubAdminServiceException {
        try {
            return clubAdminDao.findByUserId(userId)
                    .orElseThrow(DataNotFoundException::new);
        } catch (DataNotFoundException e) {
            log.error("ClubAdmin not found for userId: {}", userId);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.CLUB_ADMIN_NOT_FOUND);
        } catch (ClubAdminDAOException e) {
            log.error("Exception while getting ClubAdmin for userId: {}", userId, e);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.GET_CLUB_ADMIN_FAILED);
        }
    }

    @Override
    public ClubAdmin getClubAdminById(Long adminId) throws ClubAdminServiceException {
        try {
            return clubAdminDao.getById(adminId);
        } catch (DataNotFoundException e){
            log.error("ClubAdmin not found with adminId: {}", adminId);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.CLUB_ADMIN_NOT_FOUND);
        } catch (ClubAdminDAOException e){
            log.error("Exception while getting ClubAdmin with adminId: {}", adminId, e);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.GET_CLUB_ADMIN_FAILED);
        }
    }

    @Override
    public ClubAdmin updateClubAdminDetails(Long adminId, UpdateClubAdminRequest request)
            throws ClubAdminServiceException {
        try {
            ClubAdmin clubAdmin = clubAdminDao.getById(adminId);
            if (StringUtils.hasText(request.getEmail())) {
                clubAdmin.setEmail(request.getEmail());
            }

            if (StringUtils.hasText(request.getName())) {
                clubAdmin.setName(request.getName());
            }

            clubAdminDao.update(adminId, clubAdmin);
            return clubAdmin;
        } catch (DataNotFoundException e){
            log.error("ClubAdmin not found for update with adminId: {}", adminId);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.CLUB_ADMIN_NOT_FOUND);
        } catch (ClubAdminDAOException e){
            log.error("Exception while updating ClubAdmin details with adminId: {}",
                    adminId, e);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.UPDATE_CLUB_ADMIN_FAILED);
        }
    }

    @Override
    public void deleteClubAdmin(Long adminId) throws ClubAdminServiceException {
        try {
            clubAdminDao.deleteById(adminId);
        } catch (ClubAdminDAOException e){
            log.error("Exception while deleting ClubAdmin with adminId: {}", adminId, e);
            throw new ClubAdminServiceException(
                    ClubAdminServiceExceptionType.DELETE_CLUB_ADMIN_FAILED);
        }
    }
}
