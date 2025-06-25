package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.booking.repository.entity.PaymentStatus;
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
