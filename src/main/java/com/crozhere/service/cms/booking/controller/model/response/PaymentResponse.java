package com.crozhere.service.cms.booking.controller.model.response;

import com.crozhere.service.cms.booking.repository.entity.PaymentMode;
import com.crozhere.service.cms.booking.repository.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long intentId;
    private Double amount;
    private PaymentMode paymentMode;
    private PaymentStatus status;
}
