package com.crozhere.service.cms.club.repository.dao;

import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.*;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;

import java.util.List;

public interface ClubDao {
    // CLUB LEVEL METHODS
    void saveClub(Club club) throws ClubDAOException;
    void updateClub(Long clubId, Club club) throws DataNotFoundException, ClubDAOException;
    void softDeleteClub(Long clubId) throws DataNotFoundException, ClubDAOException;
    void deleteClub(Long clubId) throws ClubDAOException;

    Club getClubById(Long clubId) throws DataNotFoundException, ClubDAOException;
    Club getDetailedClubById(Long clubId) throws DataNotFoundException, ClubDAOException;
    List<Club> getClubsByAdminId(Long clubAdminId) throws ClubDAOException;
    List<Club> getClubsByIds(List<Long> clubIds) throws ClubDAOException;

    // STATION LEVEL METHODS
    void saveStation(Station station) throws ClubDAOException;
    void updateStation(Long stationId, Station station) throws ClubDAOException;
    void softDeleteStation(Long stationId) throws DataNotFoundException, ClubDAOException;
    void softDeleteStations(List<Long> stationIds) throws ClubDAOException;
    void deleteStation(Long stationId) throws ClubDAOException;
    void deleteStations(List<Long> stationIds) throws ClubDAOException;

    Station getStationById(Long stationId) throws DataNotFoundException, ClubDAOException;
    List<Station> getStationsByIds(List<Long> stationIds) throws ClubDAOException;
    List<Station> getStationsByClubId(Long clubId) throws ClubDAOException;
    List<Station> getStationsByClubIds(List<Long> clubIds) throws ClubDAOException;


    // RATE-CARD LEVEL METHODS
    void saveRateCard(RateCard rateCard) throws ClubDAOException;
    void updateRateCard(Long rateCardId, RateCard rateCard) throws DataNotFoundException, ClubDAOException;
    void softDeleteRateCard(Long rateCardId) throws DataNotFoundException, ClubDAOException;
    void softDeleteRateCards(List<Long> rateCardIds) throws DataNotFoundException, ClubDAOException;
    void deleteRateCard(Long rateCardId) throws ClubDAOException;
    void deleteRateCards(List<Long> rateCardIds) throws ClubDAOException;

    RateCard getRateCardById(Long rateCardId) throws DataNotFoundException, ClubDAOException;
    List<RateCard> getRateCardsByIds(List<Long> rateCardIds) throws ClubDAOException;
    List<RateCard> getRateCardsByClubId(Long clubId) throws ClubDAOException;
    RateCard getDetailedRateCardById(Long rateCardId) throws DataNotFoundException, ClubDAOException;
    List<RateCard> getDetailedRateCardsByIds(List<Long> rateCardIds) throws ClubDAOException;
    List<RateCard> getDetailedRateCardsByClubId(Long clubId) throws ClubDAOException;


    // RATE LEVEL METHODS
    void saveRate(Rate rate) throws ClubDAOException;
    void updateRate(Long rateId, Rate rate) throws DataNotFoundException, ClubDAOException;
    void softDeleteRate(Long rateId) throws DataNotFoundException, ClubDAOException;
    void softDeleteRates(List<Long> rateIds) throws DataNotFoundException, ClubDAOException;
    void deleteRate(Long rateId) throws ClubDAOException;
    void deleteRates(List<Long> rateIds) throws ClubDAOException;

    Rate getRateById(Long rateId) throws DataNotFoundException, ClubDAOException;
    List<Rate> getRatesByRateIds(List<Long> rateIds) throws ClubDAOException;
    List<Rate> getRatesByRateCardId(Long rateCardId) throws ClubDAOException;
    List<Rate> getRatesByRateCardIds(List<Long> rateCardIds) throws ClubDAOException;

    // RATE CHARGE LEVEL METHODS
    void saveRateCharge(RateCharge rateCharge) throws ClubDAOException;
    void updateRateCharge(Long rateChargeId, RateCharge rateCharge) throws ClubDAOException;
    void softDeleteRateCharge(Long rateChargeId) throws DataNotFoundException, ClubDAOException;
    void deleteRateCharge(Long rateChargeId) throws ClubDAOException;

    RateCharge getRateChargeById(Long rateChargeId) throws DataNotFoundException, ClubDAOException;
}
