package com.crozhere.service.cms.club.controller.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHours {
    private String openTime;
    private String closeTime;

    private static final DateTimeFormatter timeFormatter
            = DateTimeFormatter.ofPattern("HH:mm");

    public static String convertLocalTimeToString(LocalTime localTime){
        return localTime.format(timeFormatter);
    }

    public static LocalTime convertStringToLocalTime(String stringTime){
        return LocalTime.parse(stringTime, timeFormatter);
    }
}
