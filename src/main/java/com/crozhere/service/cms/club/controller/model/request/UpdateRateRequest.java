package com.crozhere.service.cms.club.controller.model.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRateRequest {
    private String rateName;
    private String rateDescription;
    private List<UpdateChargeRequest> updateChargeRequests;
}
