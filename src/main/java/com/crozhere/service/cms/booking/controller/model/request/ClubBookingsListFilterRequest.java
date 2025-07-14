package com.crozhere.service.cms.booking.controller.model.request;

import com.crozhere.service.cms.booking.repository.entity.BookingStatus;
import com.crozhere.service.cms.booking.repository.entity.BookingType;
import com.crozhere.service.cms.club.repository.entity.StationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubBookingsListFilterRequest {

    @Schema(
            description = "Start of booking time range (ISO 8601 format, no seconds)",
            example = "2025-07-01T09:00"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fromDateTime;

    @Schema(
            description = "End of booking time range (ISO 8601 format, no seconds)",
            example = "2025-07-10T22:30"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime toDateTime;

    @Schema(
            description = "List of station types to filter by",
            example = "[\"PC\", \"PS4\"]"
    )
    private List<StationType> stationTypes;

    @Schema(
            description = "List of booking statuses to filter by",
            example = "[\"CONFIRMED\", \"CANCELLED\"]"
    )
    private List<BookingStatus> bookingStatuses;

    @Schema(
            description = "List of booking types to filter by",
            example = "[\"GRP\", \"IND\"]"
    )
    private List<BookingType> bookingTypes;
}
