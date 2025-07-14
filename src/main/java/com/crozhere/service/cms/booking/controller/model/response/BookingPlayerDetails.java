package com.crozhere.service.cms.booking.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPlayerDetails {
    private Long playerId;
    private String playerPhoneNumber;
    private String name;
}
