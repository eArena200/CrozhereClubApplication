package com.crozhere.service.cms.club.controller;

import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.controller.model.response.ClubResponse;
import com.crozhere.service.cms.club.controller.model.response.StationResponse;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Station;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clubs")
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
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(getClubResponse(club));

        } catch (ClubServiceException e) {
            log.error("Exception in CreateClub for request: {}", createClubRequest.toString());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ClubResponse>> getClubs(
            @RequestParam(value = "clubAdminId", required = false) String clubAdminId){
        try {
            List<Club> clubs;
            if (clubAdminId != null) {
                clubs = clubService.getClubsByAdmin(clubAdminId);
            } else {
                clubs = clubService.getAllClubs();
            }
            List<ClubResponse> clubsResponse = clubs.stream()
                    .map(this::getClubResponse)
                    .toList();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(clubsResponse);

        } catch (ClubServiceException clubServiceException){
            log.error("Exception in GetAllClub request");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubResponse> getClubById(
            @PathVariable("clubId") String clubId) {
        try {
            Club club = clubService.getClubById(clubId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(getClubResponse(club));

        } catch (ClubServiceException e) {
            log.error("Exception in GetClubById request for clubId: {}", clubId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }


    @PutMapping("/{clubId}")
    public ResponseEntity<ClubResponse> updateClub(
            @PathVariable("clubId") String clubId,
            @RequestBody UpdateClubRequest updateClubRequest) {
        try {
            Club club = clubService.updateClub(clubId, updateClubRequest);
            return ResponseEntity.
                    status(HttpStatus.OK)
                    .body(getClubResponse(club));

        } catch (ClubServiceException e) {
            log.error("Exception in UpdateClub request for clubId: {}", clubId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(
            @PathVariable("clubId") String clubId) {
        try {
            clubService.deleteClub(clubId);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();

        } catch (ClubServiceException e) {
            log.error("Exception in DeleteClub request for clubId: {}", clubId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> addStation(
            @RequestBody AddStationRequest addStationRequest){
        try {
            Station station = clubService.addStation(addStationRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(getStationResponse(station));

        } catch (ClubServiceException clubServiceException){
            log.error("Exception in AddStation request: {}", addStationRequest.toString());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/stations/{stationId}")
    public ResponseEntity<StationResponse> getStation(
            @PathVariable("stationId") String stationId){
        try {
            Station station = clubService.getStation(stationId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(getStationResponse(station));

        } catch (ClubServiceException clubServiceException){
            log.error("Exception in GetStation request for stationId: {}", stationId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PutMapping("/stations/{stationId}")
    public ResponseEntity<StationResponse> updateStation(
            @PathVariable("stationId") String stationId,
            @RequestBody UpdateStationRequest updateStationRequest){
        try {
            Station station =
                    clubService.updateStation(stationId, updateStationRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(getStationResponse(station));

        } catch (ClubServiceException clubServiceException){
            log.error("Exception in UpdateStation request for stationId: {}", stationId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity<Void> deleteStation(
            @PathVariable String stationId){
        try {
            clubService.deleteStation(stationId);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();

        } catch (ClubServiceException clubServiceException){
            log.error("Exception in DeleteStation request for stationId: {}", stationId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> getStationsByClubId(
            @RequestParam(value = "clubId") String clubId){
        try {
            List<StationResponse> stationsResponse =
                    clubService.getStationsByClubId(clubId)
                            .stream()
                            .map(this::getStationResponse)
                            .toList();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(stationsResponse);

        } catch (ClubServiceException clubServiceException) {
            log.error("Exception in GetStationsByClubId request for clubId: {}", clubId);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private ClubResponse getClubResponse(Club club) {
        return ClubResponse.builder()
                .clubId(club.getClubId())
                .name(club.getName())
                .clubAdminId(club.getClubAdminId())
                .build();
    }

    private StationResponse getStationResponse(Station station) {
        return StationResponse.builder()
                .stationId(station.getStationId())
                .clubId(station.getClubId())
                .stationName(station.getStationName())
                .stationType(station.getStationType())
                .isAvailable(station.getIsAvailable())
                .build();
    }

}
