package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @JoinColumn(name = "club_admin_id", nullable = false)
    private Long clubAdminId;

    @Column(name = "club_name", unique = true, nullable = false)
    private String clubName;

    @Column(name = "club_description")
    private String clubDescription;

    @Embedded
    private ClubAddress clubAddress;

    @Embedded
    private ClubContact clubContact;

    @Embedded
    private ClubOperatingHours clubOperatingHours;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Station> stations = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<RateCard> rateCards = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        if(this.stations == null){
            this.stations = new ArrayList<>();
        }

        if(this.rateCards == null){
            this.rateCards = new ArrayList<>();
        }

        if(this.clubDescription == null){
            this.clubDescription = "Club's Tag Line";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
