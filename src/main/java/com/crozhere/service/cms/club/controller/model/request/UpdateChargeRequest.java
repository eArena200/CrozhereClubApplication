package com.crozhere.service.cms.club.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.ChargeType;
import com.crozhere.service.cms.club.repository.entity.ChargeUnit;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChargeRequest {
    private Long chargeId;
    private Long rateId;
    private ChargeType chargeType;
    private ChargeUnit chargeUnit;
    private Double amount;
    private String startTime;
    private String endTime;
    private Integer minPlayers;
    private Integer maxPlayers;
}
