package com.crozhere.service.cms.club.controller.model.response;

import com.crozhere.service.cms.club.repository.entity.ChargeType;
import com.crozhere.service.cms.club.repository.entity.ChargeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateChargeResponse {
    private Long chargeId;
    private Long rateId;
    private ChargeType chargeType;
    private ChargeUnit chargeUnit;
    private String chargeName;
    private Double amount;
    private String startTime;
    private String endTime;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Set<DayOfWeek> daysOfWeek;
    private Boolean isDeleted;
}
