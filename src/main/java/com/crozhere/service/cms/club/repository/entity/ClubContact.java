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
public class ClubContact {
    @Column(name = "contact_primary", nullable = false)
    private String primaryContact;

    @Column(name = "contact_secondary")
    private String secondaryContact;
}
