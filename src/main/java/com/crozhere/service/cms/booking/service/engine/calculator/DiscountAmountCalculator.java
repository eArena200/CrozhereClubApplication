package com.crozhere.service.cms.booking.service.engine.calculator;

import com.crozhere.service.cms.booking.repository.entity.AmountCategory;
import com.crozhere.service.cms.booking.repository.entity.BookingAmountItem;
import com.crozhere.service.cms.booking.service.engine.BookingContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class DiscountAmountCalculator implements AmountCalculator {
    @Override
    public List<BookingAmountItem> calculate(BookingContext context) {
        return List.of();
    }

    @Override
    public AmountCategory getCategory() {
        return AmountCategory.DISCOUNT;
    }
}
