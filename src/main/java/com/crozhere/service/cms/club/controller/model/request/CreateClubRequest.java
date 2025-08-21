package com.crozhere.service.cms.club.controller.model.request;

import com.crozhere.service.cms.club.controller.model.ClubAddressDetails;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClubRequest {
    private String clubName;
    private String clubDescription;
    private ClubAddressDetails clubAddressDetails;
    private OperatingHours operatingHours;
    private String primaryContact;
    private String secondaryContact;
}
