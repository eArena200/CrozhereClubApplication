package com.crozhere.service.cms.booking.controller.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedListBookingsResponse {
    private List<BookingResponse> bookings;
    private Long totalCount;
}
