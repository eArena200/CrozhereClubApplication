package com.crozhere.service.cms.payment.controller.model.response;

import com.crozhere.service.cms.payment.repository.entity.PaymentMode;
import com.crozhere.service.cms.payment.repository.entity.PaymentStatus;
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
