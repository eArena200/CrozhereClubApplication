package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubResponse {
    private Long clubId;
    private Long clubAdminId;
    private String clubName;
    private String clubDescription;
    private ClubAddressDetails clubAddress;
    private OperatingHours operatingHours;
    private String primaryContact;
    private String secondaryContact;
}
