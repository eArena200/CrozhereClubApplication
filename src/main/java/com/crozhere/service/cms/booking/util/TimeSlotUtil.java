package com.crozhere.service.cms.booking.util;

import com.crozhere.service.cms.booking.controller.model.request.SearchWindow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeSlotUtil {

    public static List<TimeSlot> mergeIntervals(List<TimeSlot> slots) {
        if (slots.isEmpty()) return List.of();

        slots.sort(Comparator.comparing(TimeSlot::getStartTime));
        List<TimeSlot> merged = new ArrayList<>();
        TimeSlot current = slots.get(0);

        for (int i = 1; i < slots.size(); i++) {
            TimeSlot next = slots.get(i);
            if (!current.getEndTime().isBefore(next.getStartTime())) {
                current = TimeSlot.builder()
                        .startTime(current.getStartTime())
                        .endTime(max(current.getEndTime(), next.getEndTime()))
                        .build();
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    public static List<TimeSlot> invert(List<TimeSlot> busy,
                                        LocalDateTime windowStart, LocalDateTime windowEnd) {
        List<TimeSlot> free = new ArrayList<>();
        LocalDateTime current = windowStart;

        for (TimeSlot busySlot : busy) {
            if (current.isBefore(busySlot.getStartTime())) {
                free.add(TimeSlot.builder()
                        .startTime(current)
                        .endTime(busySlot.getStartTime())
                        .build());
            }
            current = max(current, busySlot.getEndTime());
        }

        if (current.isBefore(windowEnd)) {
            free.add(TimeSlot.builder()
                    .startTime(current)
                    .endTime(windowEnd)
                    .build());
        }

        return free;
    }

    public static List<TimeSlot> intersectFreeSlots(
            List<List<TimeSlot>> allFree, int durationInHours, SearchWindow window) {

        List<TimeSlot> result = allFree.get(0);
        for (int i = 1; i < allFree.size(); i++) {
            result = intersectTwo(result, allFree.get(i));
        }

        LocalDateTime validStart = window.getDateTime();
        LocalDateTime validEnd = validStart.plusHours(window.getWindowHrs());
        long minDuration = durationInHours * 60L;
        int step = 60;

        List<TimeSlot> finalSlots = new ArrayList<>();

        for (TimeSlot interval : result) {
            LocalDateTime slotStart = max(interval.getStartTime(), validStart);
            LocalDateTime latestStart = min(interval.getEndTime().minusMinutes(minDuration), validEnd);

            while (!slotStart.isAfter(latestStart)) {
                LocalDateTime slotEnd = slotStart.plusMinutes(minDuration);
                if (!slotEnd.isAfter(interval.getEndTime())) {
                    finalSlots.add(TimeSlot.builder().startTime(slotStart).endTime(slotEnd).build());
                }
                slotStart = slotStart.plusMinutes(step);
            }
        }

        return finalSlots;
    }

    private static List<TimeSlot> intersectTwo(List<TimeSlot> a, List<TimeSlot> b) {
        List<TimeSlot> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < a.size() && j < b.size()) {
            LocalDateTime start = max(a.get(i).getStartTime(), b.get(j).getStartTime());
            LocalDateTime end = min(a.get(i).getEndTime(), b.get(j).getEndTime());

            if (start.isBefore(end)) {
                result.add(TimeSlot.builder().startTime(start).endTime(end).build());
            }

            if (a.get(i).getEndTime().isBefore(b.get(j).getEndTime())) i++;
            else j++;
        }

        return result;
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) ? a : b;
    }
}
