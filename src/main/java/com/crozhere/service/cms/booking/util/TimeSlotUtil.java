package com.crozhere.service.cms.booking.util;

import com.crozhere.service.cms.booking.controller.model.request.SearchWindow;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeSlotUtil {

    public static List<TimeSlot> mergeIntervals(List<TimeSlot> slots) {
        if (slots.isEmpty()) return List.of();

        List<TimeSlot> sortable = new ArrayList<>(slots);
        sortable.sort(Comparator.comparing(TimeSlot::getStartTime));

        List<TimeSlot> merged = new ArrayList<>();
        TimeSlot current = sortable.get(0);

        for (int i = 1; i < sortable.size(); i++) {
            TimeSlot next = sortable.get(i);
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


    public static List<TimeSlot> invert(
            List<TimeSlot> busy,
            Instant windowStart,
            Instant windowEnd
    ) {
        List<TimeSlot> free = new ArrayList<>();
        Instant current = windowStart;

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

        Instant validStart = window.getDateTime();
        Instant validEnd = validStart.plus(window.getWindowHrs(), ChronoUnit.HOURS);
        long minDuration = durationInHours * 60L;
        int step = 60;

        List<TimeSlot> finalSlots = new ArrayList<>();

        for (TimeSlot interval : result) {
            Instant slotStart = max(interval.getStartTime(), validStart);
            Instant latestStart =
                    min(
                        interval.getEndTime().minus(minDuration, ChronoUnit.MINUTES),
                        validEnd);

            while (!slotStart.isAfter(latestStart)) {
                Instant slotEnd = slotStart.plus(minDuration, ChronoUnit.MINUTES);
                if (!slotEnd.isAfter(interval.getEndTime())) {
                    finalSlots.add(TimeSlot.builder()
                            .startTime(slotStart)
                            .endTime(slotEnd)
                            .build());
                }
                slotStart = slotStart.plus(step, ChronoUnit.MINUTES);
            }
        }

        return finalSlots;
    }

    private static List<TimeSlot> intersectTwo(List<TimeSlot> a, List<TimeSlot> b) {
        List<TimeSlot> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < a.size() && j < b.size()) {
            Instant start = max(a.get(i).getStartTime(), b.get(j).getStartTime());
            Instant end = min(a.get(i).getEndTime(), b.get(j).getEndTime());

            if (start.isBefore(end)) {
                result.add(TimeSlot.builder().startTime(start).endTime(end).build());
            }

            if (a.get(i).getEndTime().isBefore(b.get(j).getEndTime())) i++;
            else j++;
        }

        return result;
    }

    private static Instant max(Instant a, Instant b) {
        return a.isAfter(b) ? a : b;
    }

    private static Instant min(Instant a, Instant b) {
        return a.isBefore(b) ? a : b;
    }
}
