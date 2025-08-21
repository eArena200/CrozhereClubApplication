package com.crozhere.service.cms.club.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateResponse {
    private Long rateId;
    private Long rateCardId;
    private String rateName;
    private String rateDescription;
    private List<RateChargeResponse> rateCharges;
}
