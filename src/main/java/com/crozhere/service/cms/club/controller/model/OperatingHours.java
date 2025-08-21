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
            return localTime.format(timeFormatter);
        }

        return null;
    }

    public static LocalTime convertStringToLocalTime(String stringTime){
        if (StringUtils.hasText(stringTime)){
            return LocalTime.parse(stringTime, timeFormatter);
        }

        return null;
    }
}
