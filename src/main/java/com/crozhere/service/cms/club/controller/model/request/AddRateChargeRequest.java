package com.crozhere.service.cms.club.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.ChargeType;
import com.crozhere.service.cms.club.repository.entity.ChargeUnit;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRateChargeRequest {
    private ChargeType chargeType;
    private String chargeName;
    private ChargeUnit chargeUnit;
    private Double amount;
    private String startTime;
    private String endTime;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Set<DayOfWeek> daysOfWeek;
}
