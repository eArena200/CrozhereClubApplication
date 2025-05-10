package com.crozhere.service.cms.club.controller;


import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.response.ClubResponse;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/club")
public class ClubController {

    private final ClubService clubService;

    @Autowired
    public ClubController(ClubService clubService){
        this.clubService = clubService;
    }


    @PostMapping
    public ResponseEntity<ClubResponse> createClub(
            @RequestBody CreateClubRequest createClubRequest) {
        try {
            Club club = clubService.createClub(createClubRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(club));
        } catch (ClubServiceException e) {
            log.error("Failed to create club: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{clubId}")
    public ResponseEntity<ClubResponse> getClubById(
            @PathVariable("clubId") String clubId) {
        try {
            Club club = clubService.getClubById(clubId);
            return ResponseEntity.ok(toResponse(club));
        } catch (ClubServiceException e) {
            log.error("Failed to get club with id {}: {}", clubId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/{clubId}")
    public ResponseEntity<ClubResponse> updateClub(
            @PathVariable("clubId") String clubId,
            @RequestBody UpdateClubRequest updateClubRequest) {
        try {
            Club updated = clubService.updateClub(clubId, updateClubRequest);
            return ResponseEntity.ok(toResponse(updated));
        } catch (ClubServiceException e) {
            log.error("Failed to update club with id {}: {}", clubId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(
            @PathVariable("clubId") String clubId) {
        try {
            clubService.deleteClub(clubId);
            return ResponseEntity.noContent().build();
        } catch (ClubServiceException e) {
            log.error("Failed to delete club with id {}: {}", clubId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ClubResponse toResponse(Club club) {
        return ClubResponse.builder()
                .clubId(club.getId())
                .name(club.getName())
                .clubAdminId(club.getClubAdminId())
                .build();
    }

}
