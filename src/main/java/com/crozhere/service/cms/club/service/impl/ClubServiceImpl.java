package com.crozhere.service.cms.club.service.impl;

import com.crozhere.service.cms.club.controller.model.ClubAddress;
import com.crozhere.service.cms.club.controller.model.GeoLocation;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.controller.model.request.AddStationRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateClubRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateStationRequest;
import com.crozhere.service.cms.club.repository.RateRepository;
import com.crozhere.service.cms.club.repository.dao.StationDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.dao.exception.StationDAOException;
import com.crozhere.service.cms.club.repository.entity.*;
import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.user.service.ClubAdminService;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.*;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import com.crozhere.service.cms.user.service.exception.ClubAdminServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertStringToLocalTime;

@Slf4j
@Service
public class ClubServiceImpl implements ClubService {

    private final ClubDao clubDAO;
    private final StationDao stationDAO;
    private final RateRepository rateRepository;

    private final ClubAdminService clubAdminService;

    public ClubServiceImpl(
            @Qualifier("ClubSqlDao") ClubDao clubDAO,
            @Qualifier("StationSqlDao") StationDao stationDAO,
            ClubAdminService clubAdminService,
            RateRepository rateRepository){
        this.clubDAO = clubDAO;
        this.stationDAO = stationDAO;
        this.clubAdminService = clubAdminService;
        this.rateRepository = rateRepository;
    }

    @Override
    public Club createClub(Long clubAdminId, CreateClubRequest createClubRequest)
            throws ClubServiceException {
        try {
            ClubAdmin clubAdmin =
                    clubAdminService.getClubAdminById(clubAdminId);

            Club club = Club.builder()
                    .clubAdmin(clubAdmin)
                    .clubName(createClubRequest.getClubName())
                    .street(createClubRequest.getClubAddress().getStreetAddress())
                    .city(createClubRequest.getClubAddress().getCity())
                    .state(createClubRequest.getClubAddress().getState())
                    .pincode(createClubRequest.getClubAddress().getPinCode())
                    .openTime(convertStringToLocalTime(
                                    createClubRequest.getOperatingHours().getOpenTime()))
                    .closeTime(convertStringToLocalTime(
                                    createClubRequest.getOperatingHours().getCloseTime()))
                    .primaryContact(createClubRequest.getPrimaryContact())
                    .secondaryContact(createClubRequest.getSecondaryContact())
                    .build();

            GeoLocation geoLocation = createClubRequest.getClubAddress().getGeoLocation();
            if(geoLocation != null){
                club.setLatitude(geoLocation.getLatitude());
                club.setLongitude(geoLocation.getLongitude());
            }

            clubDAO.save(club);
            return club;
        } catch (ClubAdminServiceException e){
            log.error("Exception while getting clubAdmin for clubAdminId: {}",
                    clubAdminId);
            throw new ClubServiceException(ClubServiceExceptionType.CREATE_CLUB_FAILED);
        } catch (ClubDAOException e){
            log.error("Exception while saving club for clubAdminId: {}",
                    clubAdminId);
            throw new ClubServiceException(ClubServiceExceptionType.CREATE_CLUB_FAILED);
        }
    }

    @Override
    public List<Club> getClubsByAdmin(Long clubAdminId)
            throws ClubServiceException {
        try {
            return clubDAO.getByAdmin(clubAdminId);
        } catch (ClubDAOException e){
            log.error("Exception while getting clubs for clubAdminId: {}", clubAdminId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUB_FAILED);
        }
    }

    @Override
    public Club updateClub(Long clubAdminId, Long clubId, UpdateClubRequest updateClubRequest)
            throws ClubServiceException {
        try {
            Club club = getClubById(clubId);

            if(!club.getClubAdmin().getId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminID: {} for update",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }

            if (StringUtils.hasText(updateClubRequest.getClubName())) {
                club.setClubName(updateClubRequest.getClubName());
            }

            ClubAddress address = updateClubRequest.getClubAddress();
            if (address != null) {
                if (StringUtils.hasText(address.getStreetAddress())) {
                    club.setStreet(address.getStreetAddress());
                }
                if (StringUtils.hasText(address.getCity())) {
                    club.setCity(address.getCity());
                }
                if (StringUtils.hasText(address.getState())) {
                    club.setState(address.getState());
                }
                if (StringUtils.hasText(address.getPinCode())) {
                    club.setPincode(address.getPinCode());
                }

                if (address.getGeoLocation() != null) {
                    if (address.getGeoLocation().getLatitude() != null) {
                        club.setLatitude(address.getGeoLocation().getLatitude());
                    }
                    if (address.getGeoLocation().getLongitude() != null) {
                        club.setLongitude(address.getGeoLocation().getLongitude());
                    }
                }
            }

            OperatingHours operatingHours = updateClubRequest.getOperatingHours();
            if (operatingHours != null){
                if(StringUtils.hasText(operatingHours.getOpenTime())){
                    club.setOpenTime(convertStringToLocalTime(
                            operatingHours.getOpenTime()));
                }

                if(StringUtils.hasText(operatingHours.getCloseTime())){
                    club.setCloseTime(convertStringToLocalTime(
                            operatingHours.getCloseTime()));
                }
            }

            if (StringUtils.hasText(updateClubRequest.getPrimaryContact())) {
                club.setPrimaryContact(updateClubRequest.getPrimaryContact());
            }
            if (StringUtils.hasText(updateClubRequest.getSecondaryContact())) {
                club.setSecondaryContact(updateClubRequest.getSecondaryContact());
            }

            clubDAO.update(clubId, club);
            return club;

        } catch (ClubDAOException e) {
            log.error("Exception while updating club for clubId: {}", clubId, e);
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_CLUB_FAILED);
        }
    }

    @Override
    public void deleteClub(Long clubAdminId, Long clubId)
            throws ClubServiceException {
        try {
            Club club = getClubById(clubId);
            if(!club.getClubAdmin().getId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminID: {} for delete",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }
            List<Long> stationsIds =
                    stationDAO.getStationsByClubId(clubId)
                            .stream()
                            .map(Station::getId)
                            .toList();

            stationDAO.deleteAllById(stationsIds);
            clubDAO.delete(clubId);
        } catch (ClubDAOException e){
            log.error("Exception while deleting club for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_CLUB_FAILED);
        }
    }



    @Override
    public Club getClubById(Long clubId)
            throws ClubServiceException {
        try {
            return clubDAO.getById(clubId);
        } catch (DataNotFoundException e) {
            log.error("club not found for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
        } catch (ClubDAOException e){
            log.error("Exception while getting club for clubId: {}", clubId, e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUB_FAILED);
        }
    }

    @Override
    public List<Club> getClubsByIds(List<Long> clubIds)
            throws ClubServiceException {
        try {
            if (clubIds == null || clubIds.isEmpty()) {
                return List.of();
            }
            return clubDAO.getClubsByIds(clubIds);
        } catch (ClubDAOException e) {
            log.error("Exception while getting clubs for clubIds: {}", clubIds, e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUBS_FAILED);
        }
    }

    @Override
    public List<Club> getAllClubs()
            throws ClubServiceException {
        return List.of();
    }



    @Override
    public Station addStation(Long clubAdminId, AddStationRequest addStationRequest)
            throws ClubServiceException {
        try {
            Club club = getClubById(addStationRequest.getClubId());
            if(!club.getClubAdmin().getId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminID: {} to update",
                        addStationRequest.getClubId(), clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }
            Rate rate = rateRepository
                    .findById(addStationRequest.getRateId())
                    .orElseThrow(() -> {
                        log.info("Rate not found with Id: {}", addStationRequest.getRateId());
                        return new RateCardServiceException(RateCardServiceExceptionType.RATE_NOT_FOUND);
                    });

            Station station = Station.builder()
                    .club(club)
                    .stationName(addStationRequest.getStationName())
                    .stationType(addStationRequest.getStationType())
                    .openTime(convertStringToLocalTime(
                            addStationRequest.getOperatingHours().getOpenTime()))
                    .closeTime(convertStringToLocalTime(
                            addStationRequest.getOperatingHours().getCloseTime()))
                    .rate(rate)
                    .capacity(addStationRequest.getCapacity())
                    .isActive(false)
                    .build();

            stationDAO.save(station);
            return station;
        } catch (RateCardServiceException e) {
            log.error("Exception while getting rate for rateId for new addition: {}",
                    addStationRequest.getRateId());
            throw e;
        } catch (StationDAOException e) {
            log.error("Exception while saving station for clubId: {}",
                    addStationRequest.getClubId());
            throw new ClubServiceException(ClubServiceExceptionType.ADD_STATION_FAILED);
        }
    }

    @Override
    public Station updateStation(
            Long clubAdminId,
            Long stationId,
            UpdateStationRequest updateStationRequest
    ) throws ClubServiceException {
        try {
            Station station = getStationById(stationId);
            if(station.getClub().getClubAdmin().getId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for update",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }

            if(StringUtils.hasText(updateStationRequest.getStationName())){
                station.setStationName(updateStationRequest.getStationName());
            }

            OperatingHours operatingHours = updateStationRequest.getOperatingHours();
            if( operatingHours != null){
                if(StringUtils.hasText(operatingHours.getOpenTime())){
                    station.setOpenTime(convertStringToLocalTime(operatingHours.getOpenTime()));
                }

                if(StringUtils.hasText(operatingHours.getCloseTime())){
                    station.setCloseTime(convertStringToLocalTime(operatingHours.getCloseTime()));
                }
            }

            if( updateStationRequest.getCapacity() != null){
                station.setCapacity(updateStationRequest.getCapacity());
            }

            if( updateStationRequest.getRateId() != null) {
                Rate rate = rateRepository
                        .findById(updateStationRequest.getRateId())
                        .orElseThrow(() -> {
                            log.info("Rate not found with Id for update: {}", updateStationRequest.getRateId());
                            return new RateCardServiceException(RateCardServiceExceptionType.RATE_NOT_FOUND);
                        });
                station.setRate(rate);
            }

            stationDAO.update(stationId, station);
            return station;
        } catch (RateCardServiceException e){
            log.error("Exception while getting rate for rateId for update: {}", updateStationRequest.getRateId());
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_STATION_FAILED);
        } catch (StationDAOException e){
            log.error("Exception while updating station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_STATION_FAILED);
        }
    }

    @Override
    public void deleteStation(Long clubAdminId, Long stationId)
            throws ClubServiceException {
        try {
            Station station = getStationById(stationId);
            if(station.getClub().getClubAdmin().getId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for delete",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }
            stationDAO.delete(stationId);
        } catch (StationDAOException e){
            log.error("Exception while deleting station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_STATION_FAILED, e);
        }
    }

    @Override
    public Station toggleStationStatus(Long clubAdminId, Long stationId)
            throws ClubServiceException {
        try {
            Station station = stationDAO.getById(stationId);
            if(station.getClub().getClubAdmin().getId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for toggle",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }
            station.setIsActive(!station.getIsActive());
            stationDAO.save(station);
            return station;
        } catch (StationDAOException e){
            log.error("Exception while toggling station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.TOGGLE_STATION_STATUS);
        }
    }



    @Override
    public Station getStationById(Long stationId) throws ClubServiceException {
        try {
            return stationDAO.getById(stationId);
        } catch (DataNotFoundException e){
            log.error("station not found for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
        } catch (StationDAOException e){
            log.error("Exception while getting station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATION_FAILED);
        }
    }

    @Override
    public List<Station> getStationsByClubId(Long clubId) throws ClubServiceException {
        try {
            return stationDAO.getStationsByClubId(clubId);
        } catch (StationDAOException e){
            log.error("Exception while getting stations for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATIONS_BY_CLUB_FAILED);
        }
    }

    @Override
    public List<Station> getStationsByClubIds(List<Long> clubIds)
            throws ClubServiceException {
        try {
            if (clubIds == null || clubIds.isEmpty()) {
                return List.of();
            }
            return stationDAO.getStationsByClubIds(clubIds);
        } catch (StationDAOException e) {
            log.error("Exception while getting stations for clubIds: {}", clubIds, e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATIONS_BY_CLUBS_FAILED);
        }
    }

    @Override
    public List<Station> getStationsByClubIdAndType(Long clubId, StationType stationType)
            throws ClubServiceException {
        try {
            return getStationsByClubId(clubId).stream()
                    .filter(station ->
                            station.getStationType().equals(stationType))
                    .toList();
        } catch (Exception e){
            log.error("Exception while getting stations for clubId {} and type {}", clubId, stationType);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATIONS_BY_TYPE_FAILED);
        }
    }
}
