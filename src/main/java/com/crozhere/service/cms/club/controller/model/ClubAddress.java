package com.crozhere.service.cms.club.controller.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubAddress {
    private String streetAddress;
    private String city;
    private String state;
    private String pinCode;
    private GeoLocation geoLocation;
}
