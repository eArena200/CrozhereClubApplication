package com.crozhere.service.cms.club.service.impl;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.GeoLocation;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import com.crozhere.service.cms.club.controller.model.request.*;
import com.crozhere.service.cms.club.controller.model.response.*;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.*;
import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.user.service.ClubAdminService;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.exception.*;
import com.crozhere.service.cms.user.repository.entity.ClubAdmin;
import com.crozhere.service.cms.user.service.exception.ClubAdminServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertLocalTimeToString;
import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertStringToLocalTime;

@Slf4j
@Service
public class ClubServiceImpl implements ClubService {

    private final ClubDao clubDAO;
    private final ClubAdminService clubAdminService;

    public ClubServiceImpl(
            ClubDao clubDAO,
            ClubAdminService clubAdminService
    ){
        this.clubDAO = clubDAO;
        this.clubAdminService = clubAdminService;
    }

    // CLUB LEVEL METHODS
    @Override
    @Transactional
    public ClubResponse createClub(Long clubAdminId, CreateClubRequest createClubRequest)
            throws ClubServiceException {
        try {
            ClubAdmin clubAdmin =
                    clubAdminService.getClubAdminById(clubAdminId);

            Club club = Club.builder()
                    .clubAdminId(clubAdmin.getId())
                    .clubName(createClubRequest.getClubName())
                    .clubDescription(createClubRequest.getClubDescription())
                    .clubAddress(ClubAddress.builder()
                            .street(createClubRequest.getClubAddressDetails().getStreetAddress())
                            .area(createClubRequest.getClubAddressDetails().getArea())
                            .city(createClubRequest.getClubAddressDetails().getCity())
                            .state(createClubRequest.getClubAddressDetails().getState())
                            .pincode(createClubRequest.getClubAddressDetails().getPinCode())
                            .longitude(createClubRequest.getClubAddressDetails().getGeoLocation().getLatitude())
                            .latitude(createClubRequest.getClubAddressDetails().getGeoLocation().getLatitude())
                            .build())
                    .clubOperatingHours(ClubOperatingHours.builder()
                            .openTime(convertStringToLocalTime(
                                    createClubRequest.getOperatingHours().getOpenTime()))
                            .closeTime(convertStringToLocalTime(
                                    createClubRequest.getOperatingHours().getCloseTime()))
                            .build())
                    .clubContact(ClubContact.builder()
                            .primaryContact(createClubRequest.getPrimaryContact())
                            .secondaryContact(createClubRequest.getSecondaryContact())
                            .build())
                    .build();

            clubDAO.saveClub(club);
            return buildClubResponse(club);

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
    @Transactional
    public ClubResponse updateClubDetails(Long clubAdminId, Long clubId, UpdateClubDetailsRequest updateClubDetailsRequest)
            throws ClubServiceException {
        try {
            Club club = clubDAO.getClubById(clubId);

            if(!club.getClubAdminId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminID: {} for update",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }

            if (StringUtils.hasText(updateClubDetailsRequest.getClubName())) {
                club.setClubName(updateClubDetailsRequest.getClubName());
            }

            if (StringUtils.hasText(updateClubDetailsRequest.getClubDescription())) {
                club.setClubDescription(updateClubDetailsRequest.getClubDescription());
            }

            ClubAddressDetails addressRequest = updateClubDetailsRequest.getClubAddressDetails();
            if (addressRequest != null) {
                if (StringUtils.hasText(addressRequest.getStreetAddress())) {
                    club.getClubAddress().setStreet(addressRequest.getStreetAddress());
                }
                if (StringUtils.hasText(addressRequest.getArea())) {
                    club.getClubAddress().setArea(addressRequest.getArea());
                }
                if (StringUtils.hasText(addressRequest.getCity())) {
                    club.getClubAddress().setCity(addressRequest.getCity());
                }
                if (StringUtils.hasText(addressRequest.getState())) {
                    club.getClubAddress().setState(addressRequest.getState());
                }
                if (StringUtils.hasText(addressRequest.getPinCode())) {
                    club.getClubAddress().setPincode(addressRequest.getPinCode());
                }

                if (addressRequest.getGeoLocation() != null) {
                    if (addressRequest.getGeoLocation().getLatitude() != null) {
                        club.getClubAddress().setLatitude(addressRequest.getGeoLocation().getLatitude());
                    }
                    if (addressRequest.getGeoLocation().getLongitude() != null) {
                        club.getClubAddress().setLongitude(addressRequest.getGeoLocation().getLongitude());
                    }
                }
            }

            OperatingHours operatingHours = updateClubDetailsRequest.getOperatingHours();
            if (operatingHours != null){
                if(StringUtils.hasText(operatingHours.getOpenTime())){
                    club.getClubOperatingHours().setOpenTime(convertStringToLocalTime(
                            operatingHours.getOpenTime()));
                }

                if(StringUtils.hasText(operatingHours.getCloseTime())){
                    club.getClubOperatingHours().setCloseTime(convertStringToLocalTime(
                            operatingHours.getCloseTime()));
                }
            }

            if (StringUtils.hasText(updateClubDetailsRequest.getPrimaryContact())) {
                club.getClubContact().setPrimaryContact(updateClubDetailsRequest.getPrimaryContact());
            }
            if (StringUtils.hasText(updateClubDetailsRequest.getSecondaryContact())) {
                club.getClubContact().setSecondaryContact(updateClubDetailsRequest.getSecondaryContact());
            }

            clubDAO.updateClub(clubId, club);
            return buildClubResponse(club);

        } catch (ClubDAOException e) {
            log.error("Exception while updating club for clubId: {}", clubId, e);
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_CLUB_FAILED);
        }
    }


    @Override
    @Transactional
    public void softDeleteClub(Long clubAdminId, Long clubId)
            throws ClubServiceException {
        try {
            Club club = clubDAO.getClubById(clubId);
            if(!club.getClubAdminId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminID: {} for soft-delete",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }
            clubDAO.softDeleteClub(clubId);
        } catch (DataNotFoundException e){
            log.error("Club not found for soft-deletion with clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
        } catch (ClubDAOException e){
            log.error("Exception while soft-deleting club for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_CLUB_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteClub(Long clubAdminId, Long clubId)
            throws ClubServiceException {
        try {
            Club club = clubDAO.getClubById(clubId);
            if(!club.getClubAdminId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminID: {} for delete",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }
            clubDAO.deleteClub(clubId);
        } catch (ClubDAOException e){
            log.error("Exception while deleting club for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_CLUB_FAILED);
        }
    }


    @Override
    public ClubResponse getClubById(Long clubId)
            throws ClubServiceException {
        try {
            Club club = clubDAO.getClubById(clubId);
            return buildClubResponse(club);
        } catch (DataNotFoundException e) {
            log.error("Club not found with clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
        } catch (ClubDAOException e) {
            log.error("Exception while getting club for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUB_FAILED);
        }
    }

    @Override
    public ClubDetailsResponse getDetailedClubById(Long clubId)
            throws ClubServiceException {
        try {
            Club club = clubDAO.getDetailedClubById(clubId);
            return buildClubDetailedResponse(club);
        } catch (DataNotFoundException e) {
            log.error("Club not found with clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
        } catch (ClubDAOException e) {
            log.error("Exception while getting club for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUB_FAILED);
        }
    }

    @Override
    public List<ClubResponse> getClubsByAdminId(Long clubAdminId)
            throws ClubServiceException {
        try {
            List<Club> clubs = clubDAO.getClubsByAdminId(clubAdminId);
            return clubs.stream()
                    .map(this::buildClubResponse)
                    .toList();
        } catch (ClubDAOException e){
            log.error("Exception while getting clubs for clubAdminId: {}", clubAdminId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUB_FAILED);
        }
    }

    @Override
    public List<ClubResponse> getClubsByIds(List<Long> clubIds)
            throws ClubServiceException {
        try {
            if (clubIds == null || clubIds.isEmpty()) {
                return List.of();
            }

            return clubDAO.getClubsByIds(clubIds).stream()
                    .map(this::buildClubResponse)
                    .toList();
        } catch (ClubDAOException e) {
            log.error("Exception while getting clubs for clubIds: {}", clubIds, e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_CLUBS_FAILED);
        }
    }

    // STATION LEVEL METHODS
    @Override
    @Transactional
    public StationResponse addStation(Long clubAdminId, AddStationRequest addStationRequest)
            throws ClubServiceException {
        try {
            Long clubId = addStationRequest.getClubId();
            Club club = clubDAO.getClubById(clubId);
            if(!club.getClubAdminId().equals(clubAdminId)){
                log.info("Club not found with clubId: {}, for clubAdminId: {} to update",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }

            Long rateId = addStationRequest.getRateId();
            Rate rate = clubDAO.getRateById(rateId);
            if(!rate.getRateCard().getClub().getId().equals(clubAdminId)){
                log.info("Rate not found with rateId: {}, for clubAdminId: {} to update",
                        rateId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
            }

            Station station = Station.builder()
                    .club(club)
                    .stationName(addStationRequest.getStationName())
                    .stationDescription(addStationRequest.getStationDescription())
                    .stationType(addStationRequest.getStationType())
                    .openTime(convertStringToLocalTime(
                            addStationRequest.getOperatingHours().getOpenTime()))
                    .closeTime(convertStringToLocalTime(
                            addStationRequest.getOperatingHours().getCloseTime()))
                    .rate(rate)
                    .capacity(addStationRequest.getCapacity())
                    .build();

            club.getStations().add(station);
            clubDAO.updateClub(club.getId(), club);

            return buildStationResponse(station);
        } catch (DataNotFoundException e) {
            log.error("Required data not found for station creation, {}", e.getMessage());
            throw new ClubServiceException(ClubServiceExceptionType.ADD_STATION_FAILED);
        } catch (ClubServiceException e) {
            log.error("Exception while getting club for station");
            throw e;
        } catch (ClubDAOException e) {
            log.error("Exception while adding station for clubId: {}, Error: {}",
                    addStationRequest.getClubId(), e.getMessage());
            throw new ClubServiceException(ClubServiceExceptionType.ADD_STATION_FAILED);
        }
    }

    @Override
    @Transactional
    public StationResponse updateStationDetails(
            Long clubAdminId,
            Long stationId,
            UpdateStationRequest updateStationRequest
    ) throws ClubServiceException {
        try {
            Station station = clubDAO.getStationById(stationId);
            if(!station.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for update",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }

            if(StringUtils.hasText(updateStationRequest.getStationName())){
                station.setStationName(updateStationRequest.getStationName());
            }

            if(StringUtils.hasText(updateStationRequest.getStationDescription())){
                station.setStationDescription(updateStationRequest.getStationDescription());
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
                Rate rate = clubDAO.getRateById(updateStationRequest.getRateId());
                station.setRate(rate);
            }

            clubDAO.updateStation(stationId, station);
            return buildStationResponse(station);
        } catch (DataNotFoundException e){
            log.error("Required data not found for station update, {}", e.getMessage());
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_STATION_FAILED);
        } catch (ClubServiceException e){
            log.error("Exception while updating station: {}", stationId);
            throw e;
        } catch (ClubDAOException e){
            log.error("Exception while updating station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_STATION_FAILED);
        }
    }

    @Override
    @Transactional
    public StationResponse toggleStationStatus(Long clubAdminId, Long stationId)
            throws ClubServiceException {
        try {
            Station station = clubDAO.getStationById(stationId);
            if(!station.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for toggle",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }
            Boolean currentStatus = station.getIsActive();
            station.setIsActive(!currentStatus);
            clubDAO.updateStation(stationId, station);
            return buildStationResponse(station);
        } catch (DataNotFoundException e){
            log.error("Station not found for toggle with stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
        } catch (ClubDAOException e){
            log.error("Exception while toggling station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.TOGGLE_STATION_STATUS_FAILED);
        }
    }


    @Override
    @Transactional
    public void softDeleteStation(Long clubAdminId, Long stationId)
            throws ClubServiceException {
        try {
            Station station = clubDAO.getStationById(stationId);
            if(!station.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for soft-delete",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }
            clubDAO.softDeleteStation(stationId);
        } catch (DataNotFoundException e){
            log.error("Station not found with stationId: {} for soft-delete", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_STATION_FAILED, e);
        } catch (ClubDAOException e){
            log.error("Exception while soft-deleting station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_STATION_FAILED, e);
        }
    }

    @Override
    @Transactional
    public void deleteStation(Long clubAdminId, Long stationId)
            throws ClubServiceException {
        try {
            Station station = clubDAO.getStationById(stationId);
            if(!station.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Station not found with stationId: {}, for clubAdminID: {} for delete",
                        stationId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
            }
            clubDAO.deleteStation(stationId);
        } catch (ClubDAOException e){
            log.error("Exception while deleting station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_STATION_FAILED, e);
        }
    }

    @Override
    public StationResponse getStationById(Long stationId)
            throws ClubServiceException {
        try {
            return buildStationResponse(clubDAO.getStationById(stationId));
        } catch (DataNotFoundException e){
            log.error("station not found for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.STATION_NOT_FOUND);
        } catch (ClubDAOException e){
            log.error("Exception while getting station for stationId: {}", stationId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATION_FAILED);
        }
    }

    @Override
    public List<StationResponse> getStationsByIds(List<Long> stationIds)
            throws ClubServiceException {
        try {
            return buildStationsResponse(clubDAO.getStationsByIds(stationIds));
        } catch (ClubDAOException e){
            log.error("Exception while getting stations for stationIds: {}", stationIds);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATION_FAILED);
        }
    }

    @Override
    public List<StationResponse> getStationsByClubId(Long clubId)
            throws ClubServiceException {
        try {
            return buildStationsResponse(clubDAO.getStationsByClubId(clubId));
        } catch (ClubDAOException e){
            log.error("Exception while getting stations for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATIONS_BY_CLUB_FAILED);
        }
    }

    @Override
    public List<StationResponse> getStationsByClubIds(List<Long> clubIds)
            throws ClubServiceException {
        try {
            if (clubIds == null || clubIds.isEmpty()) {
                return List.of();
            }
            return buildStationsResponse(clubDAO.getStationsByClubIds(clubIds));
        } catch (ClubDAOException e) {
            log.error("Exception while getting stations for clubIds: {}", clubIds, e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_STATIONS_BY_CLUBS_FAILED);
        }
    }

    @Override
    public List<StationResponse> getStationsByClubIdAndType(Long clubId, StationType stationType)
            throws ClubServiceException {
        return getStationsByClubId(clubId).stream()
                .filter(station ->
                        station.getStationType().equals(stationType))
                .toList();
    }

    // RATE_CARD LEVEL METHODS
    @Override
    @Transactional
    public RateCardResponse createRateCard(Long clubAdminId, Long clubId, CreateRateCardRequest request)
            throws ClubServiceException {
        try {
            Club club = clubDAO.getClubById(clubId);
            if(!club.getClubAdminId().equals(clubAdminId)){
                log.info("Club with clubId: {} Not found for clubAdminId: {}",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }
            RateCard rateCard =
                    RateCard.builder()
                            .club(club)
                            .name(request.getRateCardName())
                            .description(request.getRateCardDescription())
                            .build();

            club.getRateCards().add(rateCard);
            clubDAO.updateClub(clubId, club);
            return buildRateCardResponse(rateCard);
        } catch (DataNotFoundException e) {
            log.info("Club not found for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
        } catch (ClubServiceException e) {
            log.error("Exception in club-service while creating rate-card: [{}]", e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while creating rate-card: [{}]", e.getMessage());
            throw new ClubServiceException(ClubServiceExceptionType.CREATE_RATE_CARD_FAILED);
        }
    }

    @Override
    @Transactional
    public RateCardResponse updateRateCardDetails(
            Long clubAdminId, Long rateCardId, UpdateRateCardDetailsRequest request)
            throws ClubServiceException {
        try {
            RateCard rateCard = clubDAO.getRateCardById(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for update",
                        rateCard, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
            }

            if(StringUtils.hasText(request.getRateCardName())){
                rateCard.setName(request.getRateCardName());
            }

            if(StringUtils.hasText(request.getRateCardDescription())){
                rateCard.setDescription(request.getRateCardDescription());
            }

            clubDAO.updateRateCard(rateCardId, rateCard);
            return buildRateCardResponse(rateCard);
        } catch (DataNotFoundException e) {
            log.error("Rate card not found for update with id: {}", rateCardId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
        } catch (ClubServiceException e) {
            log.error("Exception while updating rate-card: [{}]", e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating rate-card {}, Error: [{}]", rateCardId, e.getMessage());
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_RATE_CARD_FAILED);
        }
    }

    @Override
    @Transactional
    public void softDeleteRateCard(Long clubAdminId, Long rateCardId)
            throws ClubServiceException {
        try {
            RateCard rateCard = clubDAO.getRateCardById(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for soft-delete",
                        rateCard, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
            }

            clubDAO.softDeleteRateCard(rateCardId);
        } catch (DataNotFoundException e) {
            log.error("Rate_card not found with id for soft-delete: {}", rateCardId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
        } catch (ClubDAOException e) {
            log.error("Exception while deleting rate-card {}, Error: [{}]",
                    rateCardId, e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_RATE_CARD_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteRateCard(Long clubAdminId, Long rateCardId)
            throws ClubServiceException {
        try {
            RateCard rateCard = clubDAO.getRateCardById(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for delete",
                        rateCard, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
            }

            clubDAO.deleteRateCard(rateCardId);
        } catch (DataNotFoundException e) {
            log.error("Rate_card not found with id: {}", rateCardId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
        } catch (ClubServiceException e) {
            log.error("Exception in getting rateCard {} for delete, Error: [{}]",
                    rateCardId, e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while deleting rate-card {}, Error: [{}]",
                    rateCardId, e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_RATE_CARD_FAILED);
        }
    }

    @Override
    public RateCardDetailsResponse getRateCardDetailsById(Long rateCardId) {
        try {
            return buildRateCardDetailedResponse(clubDAO.getDetailedRateCardById(rateCardId));
        } catch (DataNotFoundException e){
            log.error("Rate_card not found with Id: {}", rateCardId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
        } catch (Exception e){
            log.error("Exception while getting rate-card with Id: {} ", rateCardId, e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_RATE_CARD_FAILED);
        }
    }

    @Override
    public List<RateCardDetailsResponse> getRateCardsForClubId(Long clubId)
            throws ClubServiceException {
        try {
            return clubDAO.getDetailedRateCardsByClubId(clubId).stream()
                    .map(this::buildRateCardDetailedResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Exception while getting rate-cards for clubId: {}", clubId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_RATE_CARD_FAILED);
        }
    }


    // RATE LEVEL METHODS
    @Override
    @Transactional
    public RateResponse addRate(Long clubAdminId, Long rateCardId, AddRateRequest request)
            throws ClubServiceException {
        try {
            RateCard rateCard = clubDAO.getRateCardById(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for addRate",
                        rateCard, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
            }
            Rate rate = Rate.builder()
                    .rateCard(rateCard)
                    .name(request.getRateName())
                    .description(request.getRateDescription())
                    .build();

            rateCard.getRates().add(rate);
            clubDAO.updateRateCard(rateCardId, rateCard);
            return buildRateResponse(rate);
        } catch (DataNotFoundException e) {
            log.info("RateCard not found with Id: {}", rateCardId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CARD_NOT_FOUND);
        } catch (ClubServiceException e) {
            log.info("Exception while getting rate-card for rate addition, Error: [{}]", e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while adding rate, Error: {}", e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.ADD_RATE_FAILED);
        }
    }

    @Override
    @Transactional
    public RateResponse updateRate(Long clubAdminId, Long rateId, UpdateRateDetailsRequest request)
            throws ClubServiceException {
        try {
            Rate rate = clubDAO.getRateById(rateId);
            if(!rate.getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate with rateId: {} Not found for clubAdminId: {} for update",
                        rateId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
            }

            if(StringUtils.hasText(request.getRateName())) {
                rate.setName(request.getRateName());
            }
            if(StringUtils.hasText(request.getRateDescription())) {
                rate.setDescription(request.getRateDescription());
            }

            clubDAO.updateRate(rateId, rate);
            return buildRateResponse(rate);
        } catch (DataNotFoundException e) {
            log.error("Rate not found for update with id: {}", rateId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
        } catch (ClubServiceException e) {
            log.error("Exception while getting rate with rateId {} for update, Error:[{}] ",
                    rateId, e.getType(), e);
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating rate with rateId {}, Error: [{}]",
                    rateId, e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_RATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void softDeleteRate(Long clubAdminId, Long rateId)
            throws ClubServiceException {
        try {
            Rate rate = clubDAO.getRateById(rateId);
            if(rate.getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate with rateId: {} Not found for clubAdminId: {} for soft-delete",
                        rateId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
            }

            clubDAO.softDeleteRate(rateId);
        } catch (DataNotFoundException e) {
            log.error("Rate not found for soft-delete with id: {}", rateId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
        } catch (ClubDAOException e) {
            log.error("Exception while deleting rate {}, Error: [{}]",
                    rateId, e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_RATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteRate(Long clubAdminId, Long rateId)
            throws ClubServiceException {
        try {
            Rate rate = clubDAO.getRateById(rateId);
            if(rate.getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate with rateId: {} Not found for clubAdminId: {} for delete",
                        rateId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
            }

            clubDAO.deleteRate(rateId);
        } catch (ClubDAOException e) {
            log.error("Exception while deleting rate {}, Error: [{}]",
                    rateId, e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_RATE_FAILED);
        }
    }

    @Override
    public RateResponse getRateById(Long rateId) throws ClubServiceException {
        try {
            return buildRateResponse(clubDAO.getRateById(rateId));
        } catch (DataNotFoundException e){
            log.error("Rate not found with id: {}", rateId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
        } catch (Exception e){
            log.error("Exception while getting rate with rateId {}, Error: [{}]",
                    rateId, e.getMessage(), e);
            throw new ClubServiceException(ClubServiceExceptionType.GET_RATE_FAILED);
        }
    }

    @Override
    public List<RateResponse> getRatesForRateCard(Long rateCardId)
            throws ClubServiceException {
        try {
            return clubDAO.getRatesByRateCardId(rateCardId).stream()
                    .map(this::buildRateResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Exception while getting rates for rateCardId: {}", rateCardId);
            throw new ClubServiceException(ClubServiceExceptionType.FETCH_RATES_FAILED);
        }
    }

    @Override
    public List<RateResponse> getRatesByRateIds(List<Long> rateIds)
            throws ClubServiceException {
        try {
            return clubDAO.getRatesByRateIds(rateIds).stream()
                    .map(this::buildRateResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Exception while getting rates");
            throw new ClubServiceException(ClubServiceExceptionType.FETCH_RATES_FAILED);
        }
    }


    // RATE CHARGE LEVEL METHODS
    @Override
    public RateChargeResponse addRateCharge(Long clubAdminId, Long rateId, AddRateChargeRequest request)
            throws ClubServiceException {
        try {
            Rate rate = clubDAO.getRateById(rateId);
            if(!rate.getRateCard().getClub().getClubAdminId().equals(clubAdminId)) {
                log.info("Rate with rateId: {} not found for clubAdminId: {} for addRateCharge",
                        rateId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
            }

            RateCharge rateCharge =
                    RateCharge.builder()
                            .rate(rate)
                            .chargeType(request.getChargeType())
                            .chargeName(request.getChargeName())
                            .chargeUnit(request.getChargeUnit())
                            .amount(request.getAmount())
                            .rateChargeConstraint(
                                    RateChargeConstraint.builder()
                                            .startTime(convertStringToLocalTime(request.getStartTime()))
                                            .endTime(convertStringToLocalTime(request.getEndTime()))
                                            .minPlayers(request.getMinPlayers())
                                            .maxPlayers(request.getMaxPlayers())
                                            .applicableDays(request.getDaysOfWeek())
                                            .build())
                            .build();
            rate.getRateCharges().add(rateCharge);
            clubDAO.updateRate(rateId, rate);
            return buildChargeResponse(rateCharge);
        } catch (DataNotFoundException e) {
            log.error("Rate with rateId: {} not found for charge addition", rateId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_NOT_FOUND);
        } catch (ClubDAOException e) {
            log.error("Failed to add rate-charge to rateId: {}", rateId, e);
            throw new ClubServiceException(ClubServiceExceptionType.ADD_RATE_CHARGE_FAILED);
        }
    }

    @Override
    public RateChargeResponse updateRateCharge(Long clubAdminId, Long rateChargeId, UpdateRateChargeRequest request)
            throws ClubServiceException {
        try {
            RateCharge rateCharge = clubDAO.getRateChargeById(rateChargeId);
            if(!rateCharge.getRate().getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("RateCharge with rateChargeId: {} not found for clubAdminId: {} for updateRateCharge",
                        rateChargeId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CHARGE_NOT_FOUND);
            }

            rateCharge.setChargeType(request.getChargeType());
            rateCharge.setChargeUnit(request.getChargeUnit());
            if(StringUtils.hasText(rateCharge.getChargeName())){
                rateCharge.setChargeName(request.getChargeName());
            }
            rateCharge.setAmount(request.getAmount());


            RateChargeConstraint chargeConstraint = rateCharge.getRateChargeConstraint();
            chargeConstraint.setStartTime(convertStringToLocalTime(request.getStartTime()));
            chargeConstraint.setEndTime(convertStringToLocalTime(request.getEndTime()));
            chargeConstraint.setMinPlayers(request.getMinPlayers());
            chargeConstraint.setMaxPlayers(request.getMaxPlayers());
            chargeConstraint.setApplicableDays(request.getDaysOfWeek());

            clubDAO.updateRateCharge(rateChargeId, rateCharge);
            return buildChargeResponse(rateCharge);
        } catch (DataNotFoundException e){
            log.error("Rate-charge with rateChargeId: {} not found for update", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CHARGE_NOT_FOUND);
        } catch (ClubDAOException e){
            log.error("Failed to update rateCharge with rateChargeId: {}", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.UPDATE_RATE_CHARGE_FAILED);
        }
    }

    @Override
    public void softDeleteRateCharge(Long clubAdminId, Long rateChargeId) throws ClubServiceException {
        try {
            RateCharge rateCharge = clubDAO.getRateChargeById(rateChargeId);
            if(!rateCharge.getRate().getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("RateCharge not found with rateChargeId: {} for clubAdminId: {} for soft-delete",
                        rateChargeId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CHARGE_NOT_FOUND);
            }
            clubDAO.softDeleteRateCharge(rateChargeId);
        } catch (DataNotFoundException e) {
            log.error("RateCharge not found with rateChargeId: {} for softDelete", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CHARGE_NOT_FOUND);
        } catch (ClubDAOException e){
            log.error("Failed to soft-delete rateCharge with rateChargeId: {}", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_RATE_CHARGE_FAILED);
        }
    }

    @Override
    public void deleteRateCharge(Long clubAdminId, Long rateChargeId) throws ClubServiceException {
        try {
            RateCharge rateCharge = clubDAO.getRateChargeById(rateChargeId);
            if(!rateCharge.getRate().getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("RateCharge not found with rateChargeId: {} for clubAdminId: {} for delete",
                        rateChargeId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.RATE_CHARGE_NOT_FOUND);
            }

            clubDAO.deleteRateCharge(rateChargeId);
        } catch (ClubDAOException e){
            log.error("Failed to delete rateCharge with rateChargeId: {}", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.DELETE_RATE_CHARGE_FAILED);
        }
    }

    @Override
    public RateChargeResponse getRateChargeById(Long rateChargeId) throws ClubServiceException {
        try {
            return buildChargeResponse(clubDAO.getRateChargeById(rateChargeId));
        } catch (DataNotFoundException e) {
            log.error("Rate charge not found with rateChargeId: {}", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.RATE_CHARGE_NOT_FOUND);
        } catch (ClubDAOException e) {
            log.error("Failed to retrieve rate-charge with rateChargeId: {}", rateChargeId);
            throw new ClubServiceException(ClubServiceExceptionType.GET_RATE_CHARGE_FAILED);
        }
    }

    @Override
    public List<RateChargeResponse> getRateChargesByRateId(Long rateId) throws ClubServiceException {
        RateResponse rateResponse = getRateById(rateId);
        return rateResponse.getRateCharges();
    }


    private ClubResponse buildClubResponse(Club club){
        return ClubResponse.builder()
                .clubId(club.getId())
                .clubAdminId(club.getClubAdminId())
                .clubName(club.getClubName())
                .clubDescription(club.getClubDescription())
                .clubAddress(
                        ClubAddressDetails.builder()
                                .streetAddress(club.getClubAddress().getStreet())
                                .area(club.getClubAddress().getArea())
                                .city(club.getClubAddress().getCity())
                                .state(club.getClubAddress().getState())
                                .pinCode(club.getClubAddress().getPincode())
                                .geoLocation(
                                        GeoLocation.builder()
                                                .latitude(club.getClubAddress().getLatitude())
                                                .longitude(club.getClubAddress().getLongitude())
                                                .build())
                                .build())
                .operatingHours(
                        OperatingHours.builder()
                                .openTime(convertLocalTimeToString(
                                        club.getClubOperatingHours().getOpenTime()))
                                .closeTime(convertLocalTimeToString(
                                        club.getClubOperatingHours().getCloseTime()))
                                .build())
                .primaryContact(club.getClubContact().getPrimaryContact())
                .secondaryContact(club.getClubContact().getSecondaryContact())
                .build();
    }

    private ClubDetailsResponse buildClubDetailedResponse(Club club){
        return ClubDetailsResponse.builder()
                .clubDetails(buildClubResponse(club))
                .clubStations(club.getStations().stream()
                        .map(this::buildStationResponse)
                        .toList())
                .clubRateCards(club.getRateCards().stream()
                        .map(this::buildRateCardDetailedResponse)
                        .toList())
                .build();
    }

    private StationResponse buildStationResponse(Station station) {
        return StationResponse.builder()
                .stationId(station.getId())
                .clubId(station.getClub().getId())
                .stationName(station.getStationName())
                .stationType(station.getStationType())
                .operatingHours(
                        OperatingHours.builder()
                                .openTime(convertLocalTimeToString(station.getOpenTime()))
                                .closeTime(convertLocalTimeToString(station.getCloseTime()))
                                .build())
                .capacity(station.getCapacity())
                .isActive(station.getIsActive())
                .rateId(station.getRate().getId())
                .rateName(station.getRate().getName())
                .build();
    }

    private List<StationResponse> buildStationsResponse(List<Station> stations) {
        Map<Long, Rate> rateMap =
                clubDAO.getRatesByRateIds(
                        stations.stream()
                                .map(station -> station.getRate().getId())
                                .toList())
                        .stream()
                        .collect(Collectors.toMap(Rate::getId, Function.identity()));

        return stations.stream()
                .map(station ->
                        StationResponse.builder()
                                .stationId(station.getId())
                                .clubId(station.getClub().getId())
                                .stationName(station.getStationName())
                                .stationDescription(station.getStationDescription())
                                .stationType(station.getStationType())
                                .operatingHours(
                                        OperatingHours.builder()
                                                .openTime(convertLocalTimeToString(station.getOpenTime()))
                                                .closeTime(convertLocalTimeToString(station.getCloseTime()))
                                                .build())
                                .capacity(station.getCapacity())
                                .isActive(station.getIsActive())
                                .rateId(station.getRate().getId())
                                .rateName(rateMap.get(station.getRate().getId()).getName())
                                .build())
                .toList();
    }

    private RateCardResponse buildRateCardResponse(RateCard rateCard) {
        return RateCardResponse.builder()
                .rateCardId(rateCard.getId())
                .clubId(rateCard.getClub().getId())
                .rateCardName(rateCard.getName())
                .rateCardDescription(rateCard.getDescription())
                .build();
    }

    private RateCardDetailsResponse buildRateCardDetailedResponse(RateCard rateCard) {
        return RateCardDetailsResponse.builder()
                .rateCardId(rateCard.getId())
                .clubId(rateCard.getClub().getId())
                .rateCardName(rateCard.getName())
                .rateCardDescription(rateCard.getDescription())
                .rateList(rateCard.getRates().stream()
                        .map(this::buildRateResponse)
                        .toList())
                .build();
    }

    private RateResponse buildRateResponse(Rate rate) {
        return RateResponse.builder()
                .rateId(rate.getId())
                .rateCardId(rate.getRateCard().getId())
                .rateName(rate.getName())
                .rateDescription(rate.getDescription())
                .rateCharges(rate.getRateCharges().stream()
                        .map(this::buildChargeResponse)
                        .toList())
                .build();
    }

    private RateChargeResponse buildChargeResponse(RateCharge rateCharge) {
        return RateChargeResponse.builder()
                .chargeId(rateCharge.getId())
                .rateId(rateCharge.getRate().getId())
                .chargeType(rateCharge.getChargeType())
                .chargeName(rateCharge.getChargeName())
                .chargeUnit(rateCharge.getChargeUnit())
                .amount(rateCharge.getAmount())
                .startTime(convertLocalTimeToString(
                        rateCharge.getRateChargeConstraint().getStartTime()))
                .endTime(convertLocalTimeToString(
                        rateCharge.getRateChargeConstraint().getEndTime()))
                .minPlayers(rateCharge.getRateChargeConstraint().getMinPlayers())
                .maxPlayers(rateCharge.getRateChargeConstraint().getMaxPlayers())
                .daysOfWeek(rateCharge.getRateChargeConstraint().getApplicableDays())
                .isDeleted(rateCharge.getIsDeleted())
                .build();
    }
}
