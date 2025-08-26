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
public class ClubDetailsResponse {
    private ClubResponse clubDetails;
    private List<StationResponse> clubStations;
    private List<RateCardDetailsResponse> clubRateCards;
}
