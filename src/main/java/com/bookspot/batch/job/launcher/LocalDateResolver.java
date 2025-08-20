package com.bookspot.batch.job.launcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class LocalDateResolver {
    private final LocalDateHolder localDateHolder;
    
    public LocalDate resolveMondayOfWeek() {
        LocalDate now = localDateHolder.now();
        return now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
