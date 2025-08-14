package com.crozhere.service.cms.booking.service.engine.calculator;

import com.crozhere.service.cms.booking.repository.entity.AmountCategory;
import com.crozhere.service.cms.booking.repository.entity.BookingAmountItem;
import com.crozhere.service.cms.booking.service.engine.BookingContext;

import java.util.List;

public interface AmountCalculator {
    List<BookingAmountItem> calculate(BookingContext context);
    AmountCategory getCategory();
}
