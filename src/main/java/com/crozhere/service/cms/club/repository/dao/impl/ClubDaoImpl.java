package com.crozhere.service.cms.club.repository.dao.impl;

import com.crozhere.service.cms.club.repository.*;
import com.crozhere.service.cms.club.repository.dao.ClubDao;
import com.crozhere.service.cms.club.repository.dao.exception.ClubDAOException;
import com.crozhere.service.cms.club.repository.dao.exception.DataNotFoundException;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Rate;
import com.crozhere.service.cms.club.repository.entity.RateCard;
import com.crozhere.service.cms.club.repository.entity.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ClubDaoImpl implements ClubDao {

    private final ClubRepository clubRepository;
    private final StationRepository stationRepository;
    private final RateCardRepository rateCardRepository;
    private final RateRepository rateRepository;

    @Autowired
    public ClubDaoImpl(
            ClubRepository clubRepository,
            StationRepository stationRepository,
            RateCardRepository rateCardRepository,
            RateRepository rateRepository
    ){
        this.clubRepository = clubRepository;
        this.stationRepository = stationRepository;
        this.rateCardRepository = rateCardRepository;
        this.rateRepository = rateRepository;
    }


    // CLUB LEVEL METHODS
    @Override
    public void saveClub(Club club) throws ClubDAOException {
        try {
            clubRepository.save(club);
        } catch (Exception e) {
            log.error("Failed to save club", e);
            throw new ClubDAOException("SaveClubException");
        }
    }

    @Override
    public void updateClub(Long clubId, Club club)
            throws DataNotFoundException, ClubDAOException {
        try {
            if (!clubRepository.existsById(clubId)) {
                throw new DataNotFoundException("Club not found with ID: " + clubId);
            }
            club.setId(clubId);
            clubRepository.save(club);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update club: {}", clubId, e);
            throw new ClubDAOException("UpdateClubException");
        }
    }

    @Override
    @Transactional
    public void softDeleteClub(Long clubId)
            throws DataNotFoundException, ClubDAOException {
        try {
            Club club = clubRepository.findById(clubId)
                    .orElseThrow(() -> new DataNotFoundException("Club not found with ID: " + clubId));
            List<Long> stationIds = club.getStations().stream().map(Station::getId).toList();
            List<Long> rateCardIds = club.getRateCards().stream().map(RateCard::getId).toList();
            softDeleteStations(stationIds);
            softDeleteRateCards(rateCardIds);
            club.setIsDeleted(true);
            clubRepository.save(club);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e){
            log.error("Exception in db while soft-deleting club with id: {}", clubId, e);
            throw new ClubDAOException("SoftDeleteClubException");
        }
    }

    @Override
    public void deleteClub(Long clubId) throws ClubDAOException {
        try {
            clubRepository.deleteById(clubId);
        } catch (Exception e) {
            log.error("Failed to delete club: {}", clubId, e);
            throw new ClubDAOException("DeleteClubException");
        }
    }

    @Override
    public Club getClubById(Long clubId)
            throws DataNotFoundException, ClubDAOException {
        try {
            return clubRepository.findById(clubId)
                    .orElseThrow(() -> new DataNotFoundException("Club not found with ID: " + clubId));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get club by ID: {}", clubId, e);
            throw new ClubDAOException("GetClubException");
        }
    }

    @Override
    public Club getDetailedClubById(Long clubId)
            throws DataNotFoundException, ClubDAOException {
        try {
            return clubRepository.findDetailedClubById(clubId)
                    .orElseThrow(() -> new DataNotFoundException("Club not found with ID: " + clubId));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get detailed club by ID: {}", clubId, e);
            throw new ClubDAOException("GetDetailedClubException");
        }
    }

    @Override
    public List<Club> getClubsByAdminId(Long clubAdminId)
            throws ClubDAOException {
        try{
            return clubRepository.findByClubAdminId(clubAdminId).stream().toList();
        } catch (Exception e){
            log.error("Exception while getting club for clubAdminId: {}", clubAdminId, e);
            throw new ClubDAOException("GetClubsByAdminException");
        }
    }

    @Override
    public List<Club> getClubsByIds(List<Long> clubIds)
            throws ClubDAOException {
        try {
            if(clubIds == null || clubIds.isEmpty()){
                return List.of();
            }
            return clubRepository.findAllById(clubIds);
        } catch (Exception e) {
            log.error("Failed to fetch clubs by IDs: {}", clubIds, e);
            throw new ClubDAOException("GetClubsByIdsException", e);
        }
    }


    // STATION LEVEL METHODS
    @Override
    public void saveStation(Station station)
            throws ClubDAOException {
        try {
            // NQ -> 1
            stationRepository.save(station);
        } catch (Exception e) {
            log.error("Failed to save station: {}", station, e);
            throw new ClubDAOException("SaveStationException");
        }
    }

    @Override
    public void updateStation(Long stationId, Station station)
            throws ClubDAOException {
        try {
            // NQ -> 1 + 1
            if (!stationRepository.existsById(stationId)) {
                throw new DataNotFoundException("Station not found with ID: " + stationId);
            }
            station.setId(stationId);
            stationRepository.save(station);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update station with ID: {}", stationId, e);
            throw new ClubDAOException("UpdateStationException");
        }
    }

    @Override
    @Transactional
    public void softDeleteStation(Long stationId)
            throws DataNotFoundException, ClubDAOException {
        try {
            // NQ -> 1 + 1
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new DataNotFoundException("Station not found with ID: " + stationId));
            station.setIsActive(false);
            station.setIsDeleted(true);
            stationRepository.save(station);
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            log.error("Exception while soft-deleting station with Id: {}", stationId, e);
            throw new ClubDAOException("SoftDeleteStationException");
        }
    }

    @Override
    @Transactional
    public void softDeleteStations(List<Long> stationIds)
            throws ClubDAOException {
        try {
            // NQ -> 1 + N
            List<Station> stations = stationRepository.findAllById(stationIds);
            stations.forEach(station -> {
                station.setIsDeleted(true);
                station.setIsActive(false);
            });
            stationRepository.saveAll(stations);
        } catch (Exception e){
            log.error("Exception while soft-deleting stations with Ids: {}", stationIds, e);
            throw new ClubDAOException("SoftDeleteStationsException");
        }
    }

    @Override
    public void deleteStation(Long stationId) throws ClubDAOException {
        try {
            // NQ -> 1
            stationRepository.deleteById(stationId);
        } catch (Exception e) {
            log.error("Failed to delete station with ID: {}", stationId, e);
            throw new ClubDAOException("DeleteStationException");
        }
    }

    @Override
    public void deleteStations(List<Long> stationIds) throws ClubDAOException {
        try {
            // NQ -> 1
            stationRepository.deleteAllById(stationIds);
        } catch (Exception e) {
            log.error("Failed to delete stations with IDs: {}", stationIds, e);
            throw new ClubDAOException("DeleteStationsException");
        }
    }


    @Override
    public Station getStationById(Long stationId)
            throws DataNotFoundException, ClubDAOException {
        try {
            return stationRepository.findById(stationId)
                    .orElseThrow(() -> new DataNotFoundException("Station not found with ID: " + stationId));
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e) {
            log.error("Failed to get station with ID: {}", stationId, e);
            throw new ClubDAOException("GetStationByIdException");
        }
    }

    @Override
    public List<Station> getStationsByIds(List<Long> stationIds)
            throws ClubDAOException {
        try {
            if(stationIds == null || stationIds.isEmpty()){
                return List.of();
            }

            return stationRepository.findAllById(stationIds);
        } catch (Exception e){
            log.error("Failed to fetch stations by IDs: {}", stationIds, e);
            throw new ClubDAOException("GetStationsByIdsException", e);
        }
    }

    @Override
    public List<Station> getStationsByClubId(Long clubId)
            throws ClubDAOException {
        try {
            return stationRepository.findByClub_Id(clubId).stream().toList();
        } catch (Exception e) {
            log.error("Failed to fetch stations for club ID: {}", clubId, e);
            throw new ClubDAOException("GetStationsByClubIdException");
        }
    }

    @Override
    public List<Station> getStationsByClubIds(List<Long> clubIds)
            throws ClubDAOException {
        try {
            if (clubIds == null || clubIds.isEmpty()) {
                return List.of();
            }

            return stationRepository.findByClubIdIn(new HashSet<>(clubIds)).stream().toList();
        } catch (Exception e) {
            log.error("Failed to fetch stations for clubIds: {}", clubIds, e);
            throw new ClubDAOException("GetStationsByClubIdsException", e);
        }
    }

    // RATE-CARD LEVEL METHODS
    @Override
    public void saveRateCard(RateCard rateCard) throws ClubDAOException {
        try {
            rateCardRepository.save(rateCard);
        } catch (Exception e){
            log.error("Failed to save rate-card", e);
            throw new ClubDAOException("SaveRateCardException");
        }
    }

    @Override
    public void updateRateCard(Long rateCardId, RateCard rateCard)
            throws DataNotFoundException, ClubDAOException {
        try {
            if(!rateCardRepository.existsById(rateCardId)){
                throw new DataNotFoundException("Rate_card not found with Id: " + rateCardId);
            }
            rateCard.setId(rateCardId);
            rateCardRepository.save(rateCard);
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            log.error("Failed to update rate_card: {}", rateCardId, e);
            throw new ClubDAOException("UpdateRateCardException");
        }
    }

    @Override
    @Transactional
    public void softDeleteRateCard(Long rateCardId)
            throws DataNotFoundException, ClubDAOException {
        try {
            RateCard rateCard = rateCardRepository.findById(rateCardId)
                    .orElseThrow(() -> new DataNotFoundException("RateCard Not found with Id: " + rateCardId));
            Set<Rate> rates = rateCard.getRates();
            rates.forEach(rate -> rate.setIsDeleted(true));
            rateCard.setIsDeleted(true);
            rateCardRepository.save(rateCard);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception while soft-deleting rate-card with id: {}", rateCardId, e);
            throw new ClubDAOException("SoftDeleteRateCardException");
        }
    }

    @Override
    @Transactional
    public void softDeleteRateCards(List<Long> rateCardIds)
            throws DataNotFoundException, ClubDAOException {
        try {
            List<RateCard> rateCards = rateCardRepository.findAllById(rateCardIds);
            if (rateCards.isEmpty()) {
                throw new DataNotFoundException("No RateCards found with IDs: " + rateCardIds);
            }

            rateCards.forEach(rateCard -> {
                rateCard.setIsDeleted(true);
                Set<Rate> rates = rateCard.getRates();
                if (rates != null) {
                    rates.forEach(rate -> rate.setIsDeleted(true));
                }
            });
            rateCardRepository.saveAll(rateCards);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception while soft-deleting rate-cards with ids: {}", rateCardIds, e);
            throw new ClubDAOException("SoftDeleteRateCardsException");
        }
    }

    @Override
    public void deleteRateCard(Long rateCardId) throws ClubDAOException {
        try {
            rateCardRepository.deleteById(rateCardId);
        } catch (Exception e){
            log.error("Failed to delete rate_card: {}", rateCardId, e);
            throw new ClubDAOException("DeleteRateCardException");
        }
    }

    @Override
    public void deleteRateCards(List<Long> rateCardIds)
            throws ClubDAOException {
        try {
            rateCardRepository.deleteAllById(rateCardIds);
        } catch (Exception e){
            log.error("Failed to delete rate_cards: {}", rateCardIds, e);
            throw new ClubDAOException("DeleteRateCardsException");
        }
    }

    @Override
    public RateCard getRateCardById(Long rateCardId)
            throws DataNotFoundException, ClubDAOException {
        try {
            return rateCardRepository.findById(rateCardId)
                    .orElseThrow(() -> new DataNotFoundException("Rate-Card not found with Id: "+ rateCardId));
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            log.error("Failed to retrieve rate_card: {}", rateCardId, e);
            throw new ClubDAOException("GetRateCardByIdException");
        }
    }

    @Override
    public List<RateCard> getRateCardsByIds(List<Long> rateCardIds)
            throws ClubDAOException {
        try {
            return rateCardRepository.findAllById(rateCardIds);
        } catch (Exception e){
            log.error("Failed to retrieve rate_cards with ids: {}", rateCardIds, e);
            throw new ClubDAOException("GetRateCardsByIdsException");
        }
    }

    @Override
    public List<RateCard> getRateCardsByClubId(Long clubId) throws ClubDAOException {
        try {
            return rateCardRepository.findByClubId(clubId).stream().toList();
        } catch (Exception e){
            log.error("Failed to retrieve rate_cards by clubId: {}", clubId, e);
            throw new ClubDAOException("GetRateCardsByClubIdException");
        }
    }

    @Override
    public RateCard getDetailedRateCardById(Long rateCardId)
            throws DataNotFoundException, ClubDAOException {
        try {
            return rateCardRepository.findDetailedRateCardById(rateCardId)
                    .orElseThrow(() -> new DataNotFoundException("RateCard not found with rateCardId: {}" + rateCardId));
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception while getting detailed rate-card for id: {}", rateCardId, e);
            throw new ClubDAOException("GetDetailedRateCardByIdException");
        }
    }

    @Override
    public List<RateCard> getDetailedRateCardsByIds(List<Long> rateCardIds)
            throws ClubDAOException {
        try {
            return rateCardRepository.getDetailedRateCardsByIds(new HashSet<>(rateCardIds)).stream().toList();
        } catch (Exception e) {
            log.error("Exception while getting detailed rate-cards for ids: {}", rateCardIds, e);
            throw new ClubDAOException("GetDetailedRateCardsByIdsException");
        }
    }

    @Override
    public List<RateCard> getDetailedRateCardsByClubId(Long clubId)
            throws ClubDAOException {
        try {
            return rateCardRepository.getDetailedRateCardsByClubId(clubId).stream().toList();
        } catch (Exception e) {
            log.error("Exception while getting detailed rate-cards for clubId: {}", clubId, e);
            throw new ClubDAOException("GetDetailedRateCardByClubIdException");
        }
    }

    // RATE LEVEL METHODS
    @Override
    public void saveRate(Rate rate) throws ClubDAOException {
        try {
            rateRepository.save(rate);
        } catch (Exception e){
            log.error("Failed to save rate", e);
            throw new ClubDAOException("SaveRateException");
        }
    }

    @Override
    public void updateRate(Long rateId, Rate rate)
            throws DataNotFoundException, ClubDAOException {
        try {
            if(!rateRepository.existsById(rateId)){
                throw new DataNotFoundException("Rate not found with Id: " + rateId);
            }
            rate.setId(rateId);
            rateRepository.save(rate);
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            log.error("Failed to update rate: {}", rateId);
            throw new ClubDAOException("UpdateRateException");
        }
    }

    @Override
    @Transactional
    public void softDeleteRate(Long rateId)
            throws DataNotFoundException, ClubDAOException {
        try {
            Rate rate = rateRepository.findById(rateId)
                    .orElseThrow(() -> new DataNotFoundException("Rate not found with rateId: {}" + rateId));
            rate.setIsDeleted(true);
            rateRepository.save(rate);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception while soft-deleting rate with id: {}", rateId, e);
            throw new ClubDAOException("SoftDeleteRateException");
        }
    }

    @Override
    @Transactional
    public void softDeleteRates(List<Long> rateIds)
            throws DataNotFoundException, ClubDAOException {
        try {
            List<Rate> rates = rateRepository.findAllById(rateIds);
            rates.forEach(rate -> rate.setIsDeleted(true));
            rateRepository.saveAll(rates);
        } catch (Exception e) {
            log.error("Exception while soft-deleting rates with ids: {}", rateIds, e);
            throw new ClubDAOException("SoftDeleteRatesException");
        }
    }

    @Override
    public void deleteRate(Long rateId)
            throws DataNotFoundException, ClubDAOException {
        try {
            rateRepository.deleteById(rateId);
        } catch (Exception e){
            log.error("Exception while deleting rate with id: {}", rateId, e);
            throw new ClubDAOException("DeleteRateException");
        }
    }

    @Override
    public void deleteRates(List<Long> rateIds) throws ClubDAOException {
        try {
            rateRepository.deleteAllById(rateIds);
        } catch (Exception e) {
            log.error("Exception while deleting rates with ids: {}", rateIds, e);
            throw new ClubDAOException("SoftDeleteRateException");
        }
    }

    @Override
    public Rate getRateById(Long rateId)
            throws DataNotFoundException, ClubDAOException {
        try {
            return rateRepository.findById(rateId)
                    .orElseThrow(() -> new DataNotFoundException("Rate not found with Id: "+ rateId));
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            log.error("Failed to retrieve rate: {}", rateId, e);
            throw new ClubDAOException("GetRateByIdException");
        }
    }

    @Override
    public List<Rate> getRatesByRateIds(List<Long> rateIds)
            throws ClubDAOException {
        try {
            return rateRepository.findAllById(rateIds);
        } catch (Exception e){
            log.error("Exception while getting rates with rateIds: {}", rateIds, e);
            throw new ClubDAOException("GetRatesByRateIdsException");
        }
    }

    @Override
    public List<Rate> getRatesByRateCardId(Long rateCardId)
            throws ClubDAOException {
        try {
            return rateRepository.findByRateCardId(rateCardId).stream().toList();
        } catch (Exception e){
            log.error("Exception while getting rates for rateCardId: {}", rateCardId, e);
            throw new ClubDAOException("GetRatesByRateIdsException");
        }
    }

    @Override
    public List<Rate> getRatesByRateCardIds(List<Long> rateCardIds)
            throws ClubDAOException {
        try {
            return rateRepository.findByRateCardIdIn(new HashSet<>(rateCardIds)).stream().toList();
        } catch (Exception e){
            log.error("Exception while getting rates for rateCardIds: {}", rateCardIds, e);
            throw new ClubDAOException("GetRatesByRateCardIdsException");
        }
    }
}
