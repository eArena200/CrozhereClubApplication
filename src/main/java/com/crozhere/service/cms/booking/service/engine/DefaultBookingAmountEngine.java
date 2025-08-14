package com.crozhere.service.cms.booking.service.engine;

import com.crozhere.service.cms.booking.repository.entity.BookingAmount;
import com.crozhere.service.cms.booking.repository.entity.BookingAmountItem;
import com.crozhere.service.cms.booking.service.engine.calculator.AmountCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultBookingAmountEngine implements BookingAmountEngine{

    private final List<AmountCalculator> calculators;

    @Override
    public BookingAmount calculateAmount(BookingContext context) {
        List<BookingAmountItem> bookingAmountItems = calculators.stream()
                .map(calculator -> calculator.calculate(context))
                .filter(items -> items != null && !items.isEmpty())
                .flatMap(List::stream)
                .toList();

        BookingAmount bookingAmount = BookingAmount.builder().build();

        double chargeAmount = 0.0;
        double discountAmount = 0.0;
        double feeAmount = 0.0;
        double taxAmount = 0.0;

        for (BookingAmountItem item : bookingAmountItems) {
            item.setBookingAmount(bookingAmount);

            switch (item.getCategory()) {
                case CHARGE -> chargeAmount += safe(item.getAmount());
                case DISCOUNT -> discountAmount += safe(item.getAmount());
                case FEE -> feeAmount += safe(item.getAmount());
                case TAX -> taxAmount += safe(item.getAmount());
            }
        }

        bookingAmount.setBookingAmountItems(bookingAmountItems);
        bookingAmount.setChargeAmount(chargeAmount);
        bookingAmount.setDiscountAmount(discountAmount);
        bookingAmount.setFeeAmount(feeAmount);
        bookingAmount.setTaxAmount(taxAmount);
        bookingAmount.setTotalAmount(
                chargeAmount - discountAmount + feeAmount + taxAmount
        );

        return bookingAmount;
    }

    private double safe(Double value) {
        return value != null ? value : 0.0;
    }
}
