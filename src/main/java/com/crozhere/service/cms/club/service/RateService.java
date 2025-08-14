package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.club.controller.model.request.AddRateRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateRateCardRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateRateCardRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateRateRequest;
import com.crozhere.service.cms.club.repository.entity.Rate;
import com.crozhere.service.cms.club.repository.entity.RateCard;

import java.util.List;

public interface RateService {
    RateCard createRateCard(Long clubAdminId, Long clubId, CreateRateCardRequest request);
    RateCard updateRateCard(Long clubAdminId, Long rateCardId, UpdateRateCardRequest request);
    void deleteRateCard(Long clubAdminId, Long rateCardId);

    RateCard getRateCard(Long rateCardId);
    List<RateCard> getRateCardsForClubId(Long clubId);


    Rate addRate(Long clubAdminId, Long rateCardId, AddRateRequest request);
    Rate updateRate(Long clubAdminId, Long rateId, UpdateRateRequest request);
    void deleteRate(Long clubAdminId, Long rateId);

    Rate getRate(Long rateId);
    List<Rate> getRatesForRateCard(Long rateCardId);
    List<Rate> getRatesByRateIds(List<Long> rateIds);
}
