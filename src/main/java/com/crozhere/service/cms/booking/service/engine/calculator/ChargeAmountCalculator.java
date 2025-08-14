package com.crozhere.service.cms.booking.service.engine.calculator;

import com.crozhere.service.cms.booking.repository.entity.*;
import com.crozhere.service.cms.booking.service.engine.BookingContext;
import com.crozhere.service.cms.club.repository.entity.ChargeType;
import com.crozhere.service.cms.club.repository.entity.Rate;
import com.crozhere.service.cms.club.repository.entity.RateCharge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
@Order(1)
public class ChargeAmountCalculator implements AmountCalculator {

    @Override
    public AmountCategory getCategory() {
        return AmountCategory.CHARGE;
    }

    @Override
    public List<BookingAmountItem> calculate(BookingContext context) {
        Map<String, List<TempBookingAmount>> subCatToAmtMap = new HashMap<>();

        BookingIntent intent = context.getBookingIntent();
        Map<Long, Long> stationToRateMap = context.getStationToRateMap();
        log.info("STATION_TO_RATE_MAP: {}", stationToRateMap.toString());
        Map<Long, Rate> rateMap = context.getRateMap();
        log.info("RATE_MAP: {}", rateMap.toString());

        Instant curr = intent.getStartTime();
        Instant end = intent.getEndTime();

        while (curr.isBefore(end)) {
            Instant next = curr.plus(30, ChronoUnit.MINUTES);

            for (BookingIntentStation station : intent.getStations()) {
                Rate rate = rateMap.get(stationToRateMap.get(station.getStationId()));
                if (rate == null || rate.getRateCharges() == null
                        || rate.getRateCharges().isEmpty()) continue;

//                log.info("STATION_ID: {} with Rate: {}", station.getStationId(), rate.getRateCharges());

                for (RateCharge charge : rate.getRateCharges()) {
                    if (charge.isRateChargeApplicable(curr, station.getPlayerCount())) {
                        TempBookingAmount temp =
                                computeTempAmount(
                                        charge,
                                        Duration.between(curr, next).toMinutes(),
                                        station.getPlayerCount()
                                );
                        String subCat = charge.getChargeType().name();
                        subCatToAmtMap
                                .computeIfAbsent(subCat, k -> new ArrayList<>())
                                .add(temp);
                    }
                }
            }

            curr = next;
        }

        log.info("SUBCAT_TO_AMT_MAP: {}", subCatToAmtMap.toString());

        // TODO: Update the description generation logic
        return subCatToAmtMap.entrySet().stream()
                .map(entry -> {
                    List<TempBookingAmount> temps = entry.getValue();
                    TempBookingAmount first = temps.get(0);

                    double totalQty = temps.stream().mapToDouble(TempBookingAmount::getQuantity).sum();
                    double totalAmt = temps.stream().mapToDouble(TempBookingAmount::getAmount).sum();

                    return BookingAmountItem.builder()
                            .category(getCategory())
                            .subcategory(entry.getKey())
                            .quantity(totalQty)
                            .qtyUnit(first.getQuantityUnit())
                            .rate(first.getRate())
                            .rateUnit(first.getRateUnit())
                            .amount(totalAmt)
                            .description("TBD")
                            .build();
                })
                .toList();
    }

    private TempBookingAmount computeTempAmount(
            RateCharge rateCharge,
            Long durationInMinutes, Integer playerCount
    ){
        double rawQty = switch (rateCharge.getUnit()){
            case PER_HOUR -> (durationInMinutes/60.0);
            case PER_PLAYER_HOUR -> {
                if(ChargeType.ADDON.equals(rateCharge.getChargeType())){
                    yield (durationInMinutes/60.0)*(playerCount - rateCharge.getMinPlayers() + 1);
                }

                yield (durationInMinutes/60.0)*(playerCount);
            }
        };
        Double roundedQty = Math.ceil(rawQty*2.0)/2.0;
        QuantityUnit quantityUnit = switch (rateCharge.getUnit()) {
            case PER_HOUR -> QuantityUnit.HOUR;
            case PER_PLAYER_HOUR -> QuantityUnit.PLAYER_HOUR;
        };

        Double rateValue = rateCharge.getAmount();
        RateUnit rateUnit = switch (rateCharge.getUnit()) {
            case PER_HOUR -> RateUnit.PER_HOUR;
            case PER_PLAYER_HOUR -> RateUnit.PER_PLAYER_HOUR;
        };
        Double amount = roundedQty * rateValue;

        return TempBookingAmount.builder()
                .quantity(roundedQty)
                .quantityUnit(quantityUnit)
                .rate(rateValue)
                .rateUnit(rateUnit)
                .amount(amount)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    private static class TempBookingAmount {
        private Double quantity;
        private QuantityUnit quantityUnit;
        private Double rate;
        private RateUnit rateUnit;
        private Double amount;
    }
}
