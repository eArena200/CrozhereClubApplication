package com.crozhere.service.cms.booking.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingIntentDetailsResponse {
    private Long intentId;
    private BookingIntentClubDetails club;
    private BookingIntentPlayerDetails player;
    private BookingIntentDetails intent;
}
