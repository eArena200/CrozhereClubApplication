package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.auth.repository.entity.User;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubAdminRequest;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import com.crozhere.service.cms.club.service.exception.ClubAdminServiceException;

public interface ClubAdminService {

    ClubAdmin createClubAdminForUser(User user) throws ClubAdminServiceException;

    ClubAdmin getClubAdminByUserId(Long userId) throws ClubAdminServiceException;

    ClubAdmin getClubAdminById(Long adminId) throws ClubAdminServiceException;

    ClubAdmin updateClubAdminDetails(Long adminId, UpdateClubAdminRequest request)
            throws ClubAdminServiceException;

    void deleteClubAdmin(Long adminId) throws ClubAdminServiceException;
}
