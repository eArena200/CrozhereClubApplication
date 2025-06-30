package com.crozhere.service.cms.payment.controller.model.request;

import com.crozhere.service.cms.payment.repository.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentRequest {
    private Long paymentId;
    private PaymentStatus paymentStatus;
}
