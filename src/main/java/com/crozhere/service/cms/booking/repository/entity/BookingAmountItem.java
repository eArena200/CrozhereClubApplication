package com.crozhere.service.cms.booking.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking_amount_item")
public class BookingAmountItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_amount_id", nullable = false)
    private BookingAmount bookingAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cat", nullable = false)
    private AmountCategory category;

    @Column(name = "sub_cat")
    private String subcategory;

    @Column(name = "description")
    private String description;

    @Column(name = "qty")
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "qty_unit")
    private QuantityUnit qtyUnit;

    @Column(name = "rate")
    private Double rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_unit")
    private RateUnit rateUnit;

    @Column(name = "amount")
    private Double amount;
}
