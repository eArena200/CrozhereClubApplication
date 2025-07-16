package com.crozhere.service.cms.booking.repository.specification;

import com.crozhere.service.cms.booking.repository.entity.Booking;
import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import com.crozhere.service.cms.booking.repository.entity.BookingType;
import com.crozhere.service.cms.club.repository.entity.StationType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.Instant;
import java.util.Set;

public class BookingSpecifications {

    public static Specification<Booking> filterBookings(
            Long clubId,
            Instant fromDateTime,
            Instant toDateTime,
            Set<StationType> stationTypes,
            Set<BookingStatus> bookingStatuses,
            Set<BookingType> bookingTypes
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("clubId"), clubId);

            if (fromDateTime != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startTime"), fromDateTime));
            }
            if (toDateTime != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startTime"), toDateTime));
            }
            if (stationTypes != null && !stationTypes.isEmpty()) {
                predicate = cb.and(predicate, root.get("stationType").in(stationTypes));
            }
            if (bookingStatuses != null && !bookingStatuses.isEmpty()) {
                predicate = cb.and(predicate, root.get("status").in(bookingStatuses));
            }
            if (bookingTypes != null && !bookingTypes.isEmpty()) {
                predicate = cb.and(predicate, root.get("bookingType").in(bookingTypes));
            }

            return predicate;
        };
    }
}

