package com.crozhere.service.cms.booking.service.engine;

import com.crozhere.service.cms.booking.repository.entity.BookingAmount;

public interface BookingAmountEngine {
    BookingAmount calculateAmount(BookingContext context);
}
