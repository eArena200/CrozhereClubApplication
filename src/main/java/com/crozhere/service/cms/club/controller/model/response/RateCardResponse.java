package com.crozhere.service.cms.club.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCardResponse {
    private Long rateCardId;
    private Long clubId;
    private String rateCardName;
    private String rateCardDescription;
}
