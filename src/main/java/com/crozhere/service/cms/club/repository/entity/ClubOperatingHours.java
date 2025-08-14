package com.crozhere.service.cms.club.repository.entity;


import java.time.LocalTime;

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
public class ClubOperatingHours {
    @Column(name = "open_hour", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_hour", nullable = false)
    private LocalTime closeTime;
}

