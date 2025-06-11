package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "club")
public class Club {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_admin_id", nullable = false)
    private ClubAdmin clubAdmin;

    @Column(name = "club_name", unique = true, nullable = false)
    private String clubName;

    @Column(name = "addr_street", nullable = false)
    private String street;

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

    @Column(name = "open_hour", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_hour", nullable = false)
    private LocalTime closeTime;

    @Column(name = "p_contact", nullable = false)
    private String primaryContact;

    @Column(name = "s_contact")
    private String secondaryContact;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
