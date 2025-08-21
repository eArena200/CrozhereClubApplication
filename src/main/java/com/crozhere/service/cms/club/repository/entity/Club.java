package com.crozhere.service.cms.club.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "club_admin_id", nullable = false)
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

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<Station> stations = new HashSet<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<RateCard> rateCards = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        defaultFill();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        defaultFill();
    }

    private void defaultFill(){
        if(this.stations == null){
            this.stations = new HashSet<>();
        }

        if(this.rateCards == null){
            this.rateCards = new HashSet<>();
        }

        if(this.isDeleted == null){
            this.isDeleted = false;
        }

        if(this.isActive == null){
            this.isActive = true;
        }

        if(this.isVerified == null){
            this.isVerified = true;
        }
    }
}
