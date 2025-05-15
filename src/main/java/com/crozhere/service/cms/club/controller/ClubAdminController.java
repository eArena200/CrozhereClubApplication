package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.request.UpdateClubAdminRequest;
import com.crozhere.service.cms.club.controller.model.response.ClubAdminResponse;
import com.crozhere.service.cms.club.repository.entity.ClubAdmin;
import com.crozhere.service.cms.club.service.ClubAdminService;
import com.crozhere.service.cms.club.service.exception.ClubAdminServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/club-admins")
public class ClubAdminController {

    private final ClubAdminService clubAdminService;

    @Autowired
    public ClubAdminController(ClubAdminService clubAdminService){
        this.clubAdminService = clubAdminService;
    }

    @GetMapping("/{clubAdminId}")
    public ResponseEntity<ClubAdminResponse> getClubAdminById(
            @PathVariable("clubAdminId") Long clubAdminId) {
        try {
            ClubAdmin clubAdmin = clubAdminService.getClubAdminById(clubAdminId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(getClubAdminResponse(clubAdmin));

        } catch (ClubAdminServiceException e) {
            log.error("Exception in GetClubAdminById request for clubAdminId: {}", clubAdminId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }


    @PutMapping("/{clubAdminId}")
    public ResponseEntity<ClubAdminResponse> updateClubAdminDetails(
            @PathVariable("clubAdminId") Long clubAdminId,
            @RequestBody UpdateClubAdminRequest updateClubAdminRequest) {
        try {
            ClubAdmin clubAdmin =
                    clubAdminService.updateClubAdminDetails(clubAdminId, updateClubAdminRequest);
            return ResponseEntity.
                    status(HttpStatus.OK)
                    .body(getClubAdminResponse(clubAdmin));

        } catch (ClubAdminServiceException e) {
            log.error("Exception in UpdateClubAdminDetails request for clubAdminId: {}", clubAdminId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    @DeleteMapping("/{clubAdminId}")
    public ResponseEntity<Void> deleteClubAdmin(
            @PathVariable("clubAdminId") Long clubAdminId) {
        try {
            clubAdminService.deleteClubAdmin(clubAdminId);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();

        } catch (ClubAdminServiceException e) {
            log.error("Exception in DeleteClubAdmin request for clubAdminId: {}", clubAdminId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private ClubAdminResponse getClubAdminResponse(ClubAdmin clubAdmin){
        return ClubAdminResponse.builder()
                .id(clubAdmin.getId())
                .name(clubAdmin.getName())
                .email(clubAdmin.getEmail())
                .phone(clubAdmin.getPhone())
                .build();
    }
}
