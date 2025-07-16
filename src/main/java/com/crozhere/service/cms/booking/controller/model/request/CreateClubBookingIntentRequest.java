package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.club.repository.entity.StationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClubBookingIntentRequest {

    @NotNull
    private Long clubId;

    @NotNull
    private String playerPhoneNumber;

    @NotNull
    private StationType stationType;

    @NotEmpty
    private List<BookingStationRequest> stations;

    @NotNull
    @Schema(
            description = "StartTime of booking time range (ISO 8601 format, no seconds)",
            example = "2025-07-01T09:00:00.000Z"
    )
    private Instant startTime;

    @NotNull
    @Schema(
            description = "StartTime of booking time range (ISO 8601 format, no seconds)",
            example = "2025-07-01T09:00:00.000Z"
    )
    private Instant endTime;
}
