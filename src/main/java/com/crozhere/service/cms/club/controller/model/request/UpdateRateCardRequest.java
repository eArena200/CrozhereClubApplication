package com.crozhere.service.cms.club.controller.model.request;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRateCardRequest {
    private String name;
    private String description;
}
