package com.crozhere.service.cms.user.service;

import com.crozhere.service.cms.user.repository.entity.User;
import com.crozhere.service.cms.user.controller.model.request.UpdateClubAdminRequest;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import com.crozhere.service.cms.user.service.exception.ClubAdminServiceException;

public interface ClubAdminService {

    ClubAdmin createClubAdminForUser(User user) throws ClubAdminServiceException;
    ClubAdmin updateClubAdminDetails(Long adminId, UpdateClubAdminRequest request)
            throws ClubAdminServiceException;
    void deleteClubAdmin(Long adminId) throws ClubAdminServiceException;

    ClubAdmin getClubAdminByUserId(Long userId) throws ClubAdminServiceException;

    ClubAdmin getClubAdminById(Long adminId) throws ClubAdminServiceException;
}
