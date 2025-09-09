package com.crozhere.service.cms.club.controller.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

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

    public static String convertLocalTimeToString(LocalTime localTime) {
        if(localTime != null){
            localTime = localTime.plusHours(5).plusMinutes(30);
            return localTime.format(timeFormatter);
        }

        return null;
    }

    public static LocalTime convertStringToLocalTime(String stringTime){
        if (StringUtils.hasText(stringTime)){
            LocalTime localTime = LocalTime.parse(stringTime, timeFormatter);
            localTime = localTime.minusHours(5).minusMinutes(30);
            return localTime;
        }

        return null;
    }
}
