package com.crozhere.service.cms.club.service.impl;

import com.crozhere.service.cms.club.controller.model.request.*;
import com.crozhere.service.cms.club.repository.RateCardRepository;
import com.crozhere.service.cms.club.repository.RateRepository;
import com.crozhere.service.cms.club.repository.entity.RateCharge;
import com.crozhere.service.cms.club.repository.entity.Club;
import com.crozhere.service.cms.club.repository.entity.Rate;
import com.crozhere.service.cms.club.repository.entity.RateCard;
import com.crozhere.service.cms.club.service.ClubService;
import com.crozhere.service.cms.club.service.RateService;
import com.crozhere.service.cms.club.service.exception.ClubServiceException;
import com.crozhere.service.cms.club.service.exception.ClubServiceExceptionType;
import com.crozhere.service.cms.club.service.exception.RateCardServiceException;
import com.crozhere.service.cms.club.service.exception.RateCardServiceExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.crozhere.service.cms.club.controller.model.OperatingHours.convertStringToLocalTime;

@Slf4j
@Service
public class RateServiceImpl implements RateService {

    private final ClubService clubService;
    private final RateCardRepository rateCardRepository;
    private final RateRepository rateRepository;

    @Autowired
    public RateServiceImpl(
            ClubService clubService,
            RateCardRepository rateCardRepository,
            RateRepository rateRepository){
        this.clubService = clubService;
        this.rateCardRepository = rateCardRepository;
        this.rateRepository = rateRepository;
    }

    // RATE-CARD METHODS
    @Override
    public RateCard createRateCard(
            Long clubAdminId,
            Long clubId,
            CreateRateCardRequest request
    ) {
        try {
            Club club = clubService.getClubById(clubId);
            if(!club.getClubAdminId().equals(clubAdminId)){
                log.info("Club with clubId: {} Not found for clubAdminId: {}",
                        clubId, clubAdminId);
                throw new ClubServiceException(ClubServiceExceptionType.CLUB_NOT_FOUND);
            }
            RateCard rateCard =
                    RateCard.builder()
                            .club(club)
                            .name(request.getName())
                            .rates(new ArrayList<>())
                            .build();
            return rateCardRepository.save(rateCard);
        } catch (ClubServiceException e) {
            log.error("Exception in club-service while creating rate-card: [{}]", e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while creating rate-card: [{}]", e.getMessage());
            throw new RateCardServiceException(RateCardServiceExceptionType.CREATE_RATE_CARD_FAILED);
        }
    }

    @Override
    public RateCard updateRateCard(
            Long clubAdminId,
            Long rateCardId,
            UpdateRateCardRequest request
    ) {
        try {
            RateCard rateCard = getRateCard(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for update",
                        rateCard, clubAdminId);
                throw new RateCardServiceException(RateCardServiceExceptionType.RATE_CARD_NOT_FOUND);
            }
            rateCard.setName(request.getName());
            return rateCardRepository.save(rateCard);
        } catch (RateCardServiceException e) {
            log.error("Exception while getting rate-card {} for update: [{}]", rateCardId ,e.getType());
            throw e;
        } catch (ClubServiceException e) {
            log.error("Exception in club-service while updating rate-card: [{}]", e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating rate-card {}, Error: [{}]", rateCardId, e.getMessage());
            throw new RateCardServiceException(RateCardServiceExceptionType.UPDATE_RATE_CARD_FAILED);
        }
    }

    @Override
    public void deleteRateCard(
            Long clubAdminId,
            Long rateCardId
    ) {
        try {
            RateCard rateCard = getRateCard(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for delete",
                        rateCard, clubAdminId);
                throw new RateCardServiceException(RateCardServiceExceptionType.RATE_CARD_NOT_FOUND);
            }
            rateCardRepository.deleteById(rateCardId);
        } catch (RateCardServiceException e) {
            log.error("Exception in getting rateCard {} for delete, Error: [{}]",
                    rateCardId, e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while deleting rate-card {}, Error: [{}]",
                    rateCardId, e.getMessage(), e);
            throw new RateCardServiceException(RateCardServiceExceptionType.DELETE_RATE_CARD_FAILED);
        }
    }

    @Override
    public RateCard getRateCard(Long rateCardId) {
        return rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> {
                    log.info("Rate-card not found for rateCardId: {}", rateCardId);
                    return new RateCardServiceException(RateCardServiceExceptionType.RATE_CARD_NOT_FOUND);
                });
    }

    @Override
    public List<RateCard> getRateCardsForClubId(Long clubId) {
        try {
            return rateCardRepository.findByClubId(clubId);
        } catch (Exception e) {
            log.error("Exception while getting rate-cards for clubId: {}", clubId);
            throw new RateCardServiceException(RateCardServiceExceptionType.GET_RATE_CARD_FAILED);
        }
    }

    // RATE METHODS
    @Override
    public Rate addRate(
            Long clubAdminId,
            Long rateCardId,
            AddRateRequest request
    ) {
        try {
            RateCard rateCard = getRateCard(rateCardId);
            if(!rateCard.getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate-card with rateCardId: {} Not found for clubAdminId: {} for addRate",
                        rateCard, clubAdminId);
                throw new RateCardServiceException(RateCardServiceExceptionType.RATE_CARD_NOT_FOUND);
            }
            Rate rate = Rate.builder()
                    .rateCard(rateCard)
                    .name(request.getRateName())
                    .rateCharges(new ArrayList<>())
                    .build();

            List<RateCharge> rateCharges = request.getCreateChargeRequests().stream()
                    .map(c -> RateCharge.builder()
                            .rate(rate)
                            .chargeType(c.getChargeType())
                            .unit(c.getChargeUnit())
                            .amount(c.getAmount())
                            .startTime(convertStringToLocalTime(c.getStartTime()))
                            .endTime(convertStringToLocalTime(c.getEndTime()))
                            .minPlayers(c.getMinPlayers())
                            .maxPlayers(c.getMaxPlayers())
                            .build())
                    .toList();

            rate.getRateCharges().addAll(rateCharges);
            return rateRepository.save(rate);
        } catch (RateCardServiceException e) {
            log.info("Exception while getting rate-card for rate addition, Error: [{}]", e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while adding rate, Error: {}", e.getMessage(), e);
            throw new RateCardServiceException(RateCardServiceExceptionType.ADD_RATE_FAILED);
        }
    }

    @Override
    public Rate updateRate(
            Long clubAdminId,
            Long rateId,
            UpdateRateRequest request
    ) {
        try {
            Rate rate = getRate(rateId);
            if(rate.getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate with rateId: {} Not found for clubAdminId: {} for update",
                        rateId, clubAdminId);
                throw new RateCardServiceException(RateCardServiceExceptionType.RATE_NOT_FOUND);
            }

            rate.setName(request.getRateName());
            Map<Long, RateCharge> existingChargesMap = rate.getRateCharges().stream()
                    .collect(Collectors.toMap(RateCharge::getId, Function.identity()));

            List<RateCharge> updatedRateCharges = new ArrayList<>();

            for (UpdateChargeRequest chargeReq : request.getUpdateChargeRequests()) {
                if (chargeReq.getChargeId() != null) {
                    RateCharge existingRateCharge = existingChargesMap.get(chargeReq.getChargeId());
                    if (existingRateCharge != null) {
                        existingRateCharge.setChargeType(chargeReq.getChargeType());
                        existingRateCharge.setUnit(chargeReq.getChargeUnit());
                        existingRateCharge.setAmount(chargeReq.getAmount());
                        existingRateCharge.setStartTime(convertStringToLocalTime(chargeReq.getStartTime()));
                        existingRateCharge.setEndTime(convertStringToLocalTime(chargeReq.getEndTime()));
                        existingRateCharge.setMinPlayers(chargeReq.getMinPlayers());
                        existingRateCharge.setMaxPlayers(chargeReq.getMaxPlayers());
                        updatedRateCharges.add(existingRateCharge);

                        existingChargesMap.remove(chargeReq.getChargeId());
                    }
                } else {
                    RateCharge newRateCharge = RateCharge.builder()
                            .rate(rate)
                            .chargeType(chargeReq.getChargeType())
                            .unit(chargeReq.getChargeUnit())
                            .amount(chargeReq.getAmount())
                            .startTime(convertStringToLocalTime(chargeReq.getStartTime()))
                            .endTime(convertStringToLocalTime(chargeReq.getEndTime()))
                            .minPlayers(chargeReq.getMinPlayers())
                            .maxPlayers(chargeReq.getMaxPlayers())
                            .build();
                    updatedRateCharges.add(newRateCharge);
                }
            }
            rate.getRateCharges().clear();
            for (RateCharge rateCharge : updatedRateCharges) {
                rateCharge.setRate(rate);
                rate.getRateCharges().add(rateCharge);
            }
            return rateRepository.save(rate);
        } catch (RateCardServiceException e) {
            log.error("Exception while getting rate with rateId {} for update, Error:[{}] ",
                    rateId, e.getType(), e);
            throw e;
        } catch (Exception e) {
            log.error("Exception while updating rate with rateId {}, Error: [{}]",
                    rateId, e.getMessage(), e);
            throw new RateCardServiceException(RateCardServiceExceptionType.UPDATE_RATE_FAILED);
        }
    }

    @Override
    public void deleteRate(
            Long clubAdminId,
            Long rateId
    ) {
        try {
            Rate rate = getRate(rateId);
            if(rate.getRateCard().getClub().getClubAdminId().equals(clubAdminId)){
                log.info("Rate with rateId: {} Not found for clubAdminId: {} for delete",
                        rateId, clubAdminId);
                throw new RateCardServiceException(RateCardServiceExceptionType.RATE_NOT_FOUND);
            }
            rateRepository.deleteById(rateId);
        } catch (RateCardServiceException e) {
            log.error("Exception in getting rate {} for delete, Error: [{}]",
                    rateId, e.getType());
            throw e;
        } catch (Exception e) {
            log.error("Exception while deleting rate {}, Error: [{}]",
                    rateId, e.getMessage(), e);
            throw new RateCardServiceException(RateCardServiceExceptionType.DELETE_RATE_FAILED);
        }
    }

    @Override
    public Rate getRate(Long rateId) {
        return rateRepository.findById(rateId)
                .orElseThrow(() -> {
                    log.error("Rate not found with rateId: {}", rateId);
                    return new RateCardServiceException(RateCardServiceExceptionType.RATE_NOT_FOUND);
                });
    }

    @Override
    public List<Rate> getRatesForRateCard(Long rateCardId) {
        try {
            return rateRepository.findByRateCardId(rateCardId);
        } catch (Exception e) {
            log.error("Exception while getting rates for rateCardId: {}", rateCardId);
            throw new RateCardServiceException(RateCardServiceExceptionType.FETCH_RATES_FAILED);
        }
    }

    @Override
    public List<Rate> getRatesByRateIds(List<Long> rateIds) {
        try {
            List<Rate> rates = rateRepository.findAllById(rateIds);
            log.info("Loaded Rates: {}", rates);
            return rates;
        } catch (Exception e) {
            log.error("Exception while getting rates");
            throw new RateCardServiceException(RateCardServiceExceptionType.FETCH_RATES_FAILED);
        }
    }
}
