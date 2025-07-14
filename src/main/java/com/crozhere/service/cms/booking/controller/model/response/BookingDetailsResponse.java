package com.crozhere.service.cms.booking.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsResponse {
    private Long bookingId;
    private BookingClubDetails club;
    private BookingPlayerDetails player;
    private BookingDetails booking;
}
