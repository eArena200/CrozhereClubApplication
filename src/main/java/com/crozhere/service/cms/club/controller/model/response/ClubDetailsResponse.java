package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDetailsResponse {
    private Long clubId;
    private String clubName;
    private ClubAddressDetails clubAddressDetails;
    private OperatingHours operatingHours;
    private String primaryContact;
    private String secondaryContact;
}
