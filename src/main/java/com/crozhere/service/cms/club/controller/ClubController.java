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
        Club club = clubService.createClub(createClubRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getClubResponse(club));
    }

    @GetMapping
    public ResponseEntity<List<ClubResponse>> getClubs(
            @RequestParam(value = "clubAdminId", required = false) Long clubAdminId){
            List<Club> clubs;
            if (clubAdminId != null) {
                clubs = clubService.getClubsByAdmin(clubAdminId);
            } else {
                clubs = clubService.getAllClubs();
            }
            List<ClubResponse> clubsResponse = clubs.stream()
                    .map(this::getClubResponse)
                    .toList();

            return ResponseEntity.status(HttpStatus.OK).body(clubsResponse);
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubResponse> getClubById(
            @PathVariable("clubId") Long clubId) {

        Club club = clubService.getClubById(clubId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getClubResponse(club));

    }


    @PutMapping("/{clubId}")
    public ResponseEntity<ClubResponse> updateClub(
            @PathVariable("clubId") Long clubId,
            @RequestBody UpdateClubRequest updateClubRequest) {

        Club club = clubService.updateClub(clubId, updateClubRequest);
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(getClubResponse(club));

    }


    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(
            @PathVariable("clubId") Long clubId) {

        clubService.deleteClub(clubId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> addStation(
            @RequestBody AddStationRequest addStationRequest){
        Station station = clubService.addStation(addStationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(getStationResponse(station));
    }

    @GetMapping("/stations/{stationId}")
    public ResponseEntity<StationResponse> getStation(
            @PathVariable("stationId") Long stationId){

        Station station = clubService.getStation(stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
    }

    @PutMapping("/stations/{stationId}")
    public ResponseEntity<StationResponse> updateStation(
            @PathVariable("stationId") Long stationId,
            @RequestBody UpdateStationRequest updateStationRequest){
        Station station =
                clubService.updateStation(stationId, updateStationRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getStationResponse(station));
    }


    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity<Void> deleteStation(
            @PathVariable Long stationId){
        clubService.deleteStation(stationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> getStationsByClubId(
            @RequestParam(value = "clubId") Long clubId){
        List<StationResponse> stationsResponse =
                clubService.getStationsByClubId(clubId)
                        .stream()
                        .map(this::getStationResponse)
                        .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stationsResponse);
    }

    private ClubResponse getClubResponse(Club club) {
        return ClubResponse.builder()
                .clubId(club.getId())
                .clubLayoutId(club.getClubLayoutId())
                .name(club.getName())
                .clubAdminId(club.getClubAdmin().getId())
                .build();
    }

    private StationResponse getStationResponse(Station station) {
        return StationResponse.builder()
                .stationId(station.getId())
                .clubId(station.getClub().getId())
                .stationName(station.getStationName())
                .stationType(station.getStationType())
                .stationLayoutId(station.getStationLayoutId())
                .stationGroupLayoutId(station.getStationGroupLayoutId())
                .isActive(station.getIsActive())
                .build();
    }

}
