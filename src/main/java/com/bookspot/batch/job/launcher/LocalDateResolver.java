package com.bookspot.batch.job.launcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class LocalDateResolver {
    public LocalDate resolveMondayOfWeek(LocalDate referenceDate) {
        return referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public LocalDate resolveMondayOfLastWeek(LocalDate referenceDate) {
        return resolveMondayOfWeek(referenceDate)
                .minusWeeks(1);
    }

    public LocalDate resolveFirstDayOfLastMonth(LocalDate referenceDate) {
        return referenceDate.minusMonths(1)
                .withDayOfMonth(1);
    }
}
