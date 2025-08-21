package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubAddress {
    @Column(name = "addr_street", nullable = false)
    private String street;

    @Column(name = "addr_area", nullable = false)
    private String area;

    @Column(name = "addr_city", nullable = false)
    private String city;

    @Column(name = "addr_state", nullable = false)
    private String state;

    @Column(name = "addr_pincode", nullable = false)
    private String pincode;

    @Column(name = "addr_geo_lat")
    private Double latitude;

    @Column(name = "addr_geo_lon")
    private Double longitude;
}
