package com.crozhere.service.cms.club.service.impl;


import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubAdminRequest;
import com.crozhere.service.cms.club.repository.dao.ClubAdminDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubAdminDAOException;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import com.crozhere.service.cms.club.service.ClubAdminService;
import com.crozhere.service.cms.club.service.exception.ClubAdminServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class ClubAdminServiceImpl implements ClubAdminService {

    private final ClubAdminDao clubAdminDao;

    public ClubAdminServiceImpl(
            @Qualifier("ClubAdminSqlDao") ClubAdminDao clubAdminDao){
        this.clubAdminDao = clubAdminDao;
    }

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
            log.error("Exception while saving newly created ClubAdmin for userId: {}", user.getId());
            throw new ClubAdminServiceException("CreateClubAdminForUserException");
        }
    }

    @Override
    public ClubAdmin getClubAdminByUserId(Long userId) throws ClubAdminServiceException {
        try {
            return clubAdminDao.findByUserId(userId)
                    .orElseThrow(ClubAdminDAOException::new);
        } catch (ClubAdminDAOException e) {
            log.error("Exception while getting ClubAdmin for userId: {}", userId);
            throw new ClubAdminServiceException("GetClubAdminByUserIdException");
        }
    }

    @Override
    public ClubAdmin getClubAdminById(Long adminId) throws ClubAdminServiceException {
        try {
            return clubAdminDao.getById(adminId);
        } catch (ClubAdminDAOException e){
            log.error("Exception while getting ClubAdmin with adminId: {}", adminId);
            throw new ClubAdminServiceException("GetClubAdminByIdException");
        }
    }

    @Override
    public ClubAdmin updateClubAdminDetails(Long adminId, UpdateClubAdminRequest request)
            throws ClubAdminServiceException {
        try {
            ClubAdmin clubAdmin = clubAdminDao.getById(adminId);
            if(StringUtils.hasText(request.getEmail())){
                clubAdmin.setEmail(request.getEmail());
            }

            if(StringUtils.hasText(request.getName())){
                clubAdmin.setName(request.getName());
            }

            clubAdminDao.update(adminId, clubAdmin);
            return clubAdmin;
        } catch (ClubAdminDAOException e){
            log.error("Exception while updating clubAdmin details with adminId: {}", adminId);
            throw new ClubAdminServiceException("UpdateClubAdminDetailsException");
        }
    }

    @Override
    public void deleteClubAdmin(Long adminId) throws ClubAdminServiceException {
        try {
            clubAdminDao.deleteById(adminId);
        } catch (ClubAdminDAOException e){
            log.error("Exception while deleting clubAdmin with adminId: {}", adminId);
            throw new ClubAdminServiceException("DeleteClubAdminException");
        }
    }
}
