package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.controller.model.ClubAddress;
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
    private ClubAddress clubAddress;
    private OperatingHours operatingHours;
    private String primaryContact;
    private String secondaryContact;
}
