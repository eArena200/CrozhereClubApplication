package com.crozhere.service.cms.booking.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAmountChargeItemDetails {
    String subCategory;
    Double rate;
    String rateUnit;
    Double qty;
    String qtyUnit;
    Double amount;
}
