package com.crozhere.service.cms.booking.service.engine;

import com.crozhere.service.cms.booking.controller.model.request.ClubDiscountRequest;
import com.crozhere.service.cms.booking.repository.entity.*;
import com.crozhere.service.cms.booking.service.engine.calculator.AmountCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultBookingAmountEngine implements BookingAmountEngine{

    private final List<AmountCalculator> calculators;

    private final String CLUB_DISCOUNT_SUB_CAT = "CLUB_DISCOUNT";

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

    @Override
    public BookingAmount applyDiscount(BookingAmount amount, ClubDiscountRequest request) {
        Double oldDiscAmount = 0.0;
        Double newDiscAmount = request.getAmount();

        Optional<BookingAmountItem> existingDiscountOpt =
                amount.getBookingAmountItems().stream()
                        .filter(item -> item.getCategory() == AmountCategory.DISCOUNT
                                && CLUB_DISCOUNT_SUB_CAT.equals(item.getSubcategory()))
                        .findFirst();

        if (existingDiscountOpt.isPresent()) {
            BookingAmountItem discountItem = existingDiscountOpt.get();
            oldDiscAmount = discountItem.getAmount();
            discountItem.setDescription(request.getDescription());
            discountItem.setAmount(newDiscAmount);
            discountItem.setQuantity(1.0);
            discountItem.setRate(newDiscAmount);
            discountItem.setRateUnit(RateUnit.PER_BOOKING);
        } else {
            BookingAmountItem discountItem = BookingAmountItem.builder()
                    .category(AmountCategory.DISCOUNT)
                    .bookingAmount(amount)
                    .subcategory(CLUB_DISCOUNT_SUB_CAT)
                    .description(request.getDescription())
                    .amount(newDiscAmount)
                    .quantity(1.0)
                    .qtyUnit(QuantityUnit.BOOKING)
                    .rate(newDiscAmount)
                    .rateUnit(RateUnit.PER_BOOKING)
                    .build();
            amount.getBookingAmountItems().add(discountItem);
        }

        Double newTotalAmount = amount.getTotalAmount() + (oldDiscAmount - newDiscAmount);
        amount.setDiscountAmount(newDiscAmount);
        amount.setTotalAmount(newTotalAmount);

        return amount;
    }

    private double safe(Double value) {
        return value != null ? value : 0.0;
    }
}
