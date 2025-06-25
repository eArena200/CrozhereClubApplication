package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.booking.repository.entity.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitPaymentRequest {
    private Long intentId;
    private Double amount;
    private PaymentMode paymentMode;
}
