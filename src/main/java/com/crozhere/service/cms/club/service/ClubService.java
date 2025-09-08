package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.club.controller.model.request.*;
import com.crozhere.service.cms.club.controller.model.response.*;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.*;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;

import java.util.List;

public interface ClubService {
    // CLUB LEVEL METHODS
    ClubResponse createClub(Long clubAdminId, CreateClubRequest createClubRequest)
            throws ClubServiceException;
    ClubResponse updateClubDetails(Long clubAdminId, Long clubId, UpdateClubDetailsRequest updateClubDetailsRequest)
            throws ClubServiceException;
    void softDeleteClub(Long clubAdminId, Long clubId) throws ClubServiceException;
    void deleteClub(Long clubAdminId, Long clubId) throws ClubServiceException;

    ClubResponse getClubById(Long clubId) throws ClubServiceException;
    ClubDetailsResponse getDetailedClubById(Long clubId) throws ClubServiceException;
    List<ClubResponse> getClubsByAdminId(Long clubAdminId) throws ClubServiceException;
    List<ClubResponse> getClubsByIds(List<Long> clubIds) throws ClubServiceException;


    // STATION LEVEL METHODS
    StationResponse addStation(Long clubAdminId, AddStationRequest addStationRequest)
            throws ClubServiceException;
    StationResponse updateStationDetails(Long clubAdminId, Long stationId, UpdateStationRequest updateStationRequest)
            throws ClubServiceException;
    StationResponse toggleStationStatus(Long clubAdminId, Long stationId) throws ClubServiceException;
    void softDeleteStation(Long clubAdminId, Long stationId) throws ClubServiceException;
    void deleteStation(Long clubAdminId, Long stationId) throws ClubServiceException;

    StationResponse getStationById(Long stationId) throws ClubServiceException;
    List<StationResponse> getStationsByIds(List<Long> stationIds) throws ClubServiceException;

    List<StationResponse> getStationsByClubId(Long clubId) throws ClubServiceException;
    List<StationResponse> getStationsByClubIds(List<Long> clubIds) throws ClubServiceException;

    List<StationResponse> getStationsByClubIdAndType(Long clubId, StationType stationType)
            throws ClubServiceException;

    // RATE-CARD LEVEL METHODS
    RateCardResponse createRateCard(Long clubAdminId, Long clubId, CreateRateCardRequest request)
            throws ClubServiceException;
    RateCardResponse updateRateCardDetails(Long clubAdminId, Long rateCardId, UpdateRateCardDetailsRequest request)
            throws ClubServiceException;
    void softDeleteRateCard(Long clubAdminId, Long rateCardId) throws ClubServiceException;
    void deleteRateCard(Long clubAdminId, Long rateCardId) throws ClubServiceException;

    RateCardDetailsResponse getRateCardDetailsById(Long rateCardId) throws ClubServiceException;
    List<RateCardDetailsResponse> getRateCardsForClubId(Long clubId) throws ClubServiceException;

    // RATE LEVEL METHODS
    RateResponse addRate(Long clubAdminId, Long rateCardId, AddRateRequest request) throws ClubServiceException;
    RateResponse updateRate(Long clubAdminId, Long rateId, UpdateRateDetailsRequest request)
            throws DataNotFoundException, ClubServiceException;
    void softDeleteRate(Long clubAdminId, Long rateId) throws ClubServiceException;
    void deleteRate(Long clubAdminId, Long rateId) throws ClubServiceException;

    RateResponse getRateById(Long rateId) throws ClubServiceException;
    List<RateResponse> getRatesByRateIds(List<Long> rateIds) throws ClubServiceException;
    List<RateResponse> getRatesForRateCard(Long rateCardId) throws ClubServiceException;

    // RATE CHARGE LEVEL METHODS
    RateChargeResponse addRateCharge(Long clubAdminId, Long rateId, AddRateChargeRequest request)
            throws ClubServiceException;
    RateChargeResponse updateRateCharge(Long clubAdminId, Long rateChargeId, UpdateRateChargeRequest request)
            throws DataNotFoundException, ClubServiceException;
    void softDeleteRateCharge(Long clubAdminId, Long rateChargeId) throws ClubServiceException;
    void deleteRateCharge(Long clubAdminId, Long rateChargeId) throws ClubServiceException;

    RateChargeResponse getRateChargeById(Long rateChargeId) throws ClubServiceException;
    List<RateChargeResponse> getRateChargesByRateId(Long rateId) throws ClubServiceException;

}
