package com.crozhere.service.cms.booking.controller.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmBookingIntentRequest {

    private Long intentId;

    private Long paymentId;

    private Boolean isCashPayment;
}
