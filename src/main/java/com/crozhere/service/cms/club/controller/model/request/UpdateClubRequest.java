package com.crozhere.service.cms.club.controller.model.request;

import com.crozhere.service.cms.club.controller.model.ClubAddress;
import com.crozhere.service.cms.club.controller.model.OperatingHours;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClubRequest {
    private String clubName;
    private Long clubAdminId;
    private ClubAddress clubAddress;
    private OperatingHours operatingHours;
    private String primaryContact;
    private String secondaryContact;
}
