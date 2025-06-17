package com.crozhere.service.cms.club.service;

import com.crozhere.service.cms.club.controller.model.request.AddRateRequest;
import com.crozhere.service.cms.club.controller.model.request.CreateRateCardRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateRateCardRequest;
import com.crozhere.service.cms.club.controller.model.request.UpdateRateRequest;
import com.crozhere.service.cms.club.repository.entity.Rate;
import com.crozhere.service.cms.club.repository.entity.RateCard;

import java.util.List;

public interface RateService {
    RateCard createRateCard(Long clubId, CreateRateCardRequest request);
    RateCard updateRateCard(Long rateCardId, UpdateRateCardRequest request);
    RateCard getRateCard(Long rateCardId);
    List<RateCard> getRateCardsForClubId(Long clubId);
    void deleteRateCard(Long rateCardId);

    Rate addRate(Long rateCardId, AddRateRequest request);
    Rate getRate(Long rateId);
    List<Rate> getRatesForRateCard(Long rateCardId);
    Rate updateRate(Long rateId, UpdateRateRequest request);
    void deleteRate(Long rateId);
}
