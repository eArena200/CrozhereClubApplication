package com.crozhere.service.cms.booking.repository.entity;

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
@Table(name = "booking_amount")
public class BookingAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Builder.Default
    @Column(name = "total_charge", nullable = false)
    private Double chargeAmount = 0.0;

    @Builder.Default
    @Column(name = "total_discount", nullable = false)
    private Double discountAmount = 0.0;

    @Builder.Default
    @Column(name = "total_fee", nullable = false)
    private Double feeAmount = 0.0;

    @Builder.Default
    @Column(name = "total_tax", nullable = false)
    private Double taxAmount = 0.0;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_amount_id")
    private List<BookingAmountItem> bookingAmountItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if(this.chargeAmount == null){
            this.chargeAmount = 0.0;
        }

        if(this.discountAmount == null){
            this.discountAmount = 0.0;
        }

        if(this.feeAmount == null){
            this.feeAmount = 0.0;
        }

        if(this.taxAmount == null){
            this.taxAmount = 0.0;
        }

    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
