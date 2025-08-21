package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
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
    private Long clubId;
    private Long clubAdminId;
    private String clubName;
    private String clubDescription;
    private ClubAddressDetails clubAddress;
    private OperatingHours operatingHours;
    private String primaryContact;
    private String secondaryContact;
    private List<StationResponse> stations;
    private List<RateCardDetailsResponse> rateCards;
}
