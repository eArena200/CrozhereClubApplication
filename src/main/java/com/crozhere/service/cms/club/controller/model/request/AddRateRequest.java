package com.crozhere.service.cms.club.controller.model.request;

import java.util.List;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRateRequest {
    private String rateName;
    private List<CreateChargeRequest> createChargeRequests;
}
