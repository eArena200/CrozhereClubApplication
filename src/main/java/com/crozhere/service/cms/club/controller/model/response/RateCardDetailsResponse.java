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
public class RateCardDetailsResponse {
    private Long rateCardId;
    private Long clubId;
    private String name;
    private List<RateResponse> rateList;
}
