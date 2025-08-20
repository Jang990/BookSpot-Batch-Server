package com.bookspot.batch.global.openapi.naru;

import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

public class NaruTop50ApiBuilder {
    private final String apiUrl;
    private final String format = "json";
    private final LocalDate periodStart;
    private final LocalDate periodEnd;

    public NaruTop50ApiBuilder(
            String apiUrl,
            LocalDate referenceDate,
            RankingConditions rankingConditions
    ) {
        Objects.requireNonNull(apiUrl);
        Objects.requireNonNull(referenceDate);
        Objects.requireNonNull(rankingConditions);

        this.apiUrl = apiUrl;
        if (isMonthly(rankingConditions.periodType())) {
            YearMonth ym = YearMonth.from(referenceDate);
            periodStart = ym.atDay(1);
            periodEnd = ym.atEndOfMonth();
        } else {
            periodStart = referenceDate.with(DayOfWeek.MONDAY);
            periodEnd = referenceDate.with(DayOfWeek.SUNDAY);
        }

    }

    private boolean isMonthly(RankingType rankingType) {
        return rankingType.equals(RankingType.MONTHLY);
    }

    public String build() {
        return apiUrl + "?"
                + "format=" + format
                + "&startDt=" + periodStart.toString()
                + "&endDt=" + periodEnd.toString();
    }
}
