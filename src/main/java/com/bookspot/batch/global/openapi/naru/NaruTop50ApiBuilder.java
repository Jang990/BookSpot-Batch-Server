package com.bookspot.batch.global.openapi.naru;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

public class NaruTop50ApiBuilder {
    private final String apiUrl;
    private final LocalDate periodStart;
    private final LocalDate periodEnd;

    private final Integer ageStart;
    private final Integer ageEnd;

    private final Integer genderCode;

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

        if (RankingAge.ALL.equals(rankingConditions.age())) {
            ageStart = null;
            ageEnd = null;
        } else {
            ageStart = rankingConditions.age().getStart();
            ageEnd = rankingConditions.age().getEnd();
        }

        this.genderCode = getGenderCode(rankingConditions.gender());

    }

    private Integer getGenderCode(RankingGender gender) {
        return switch (gender) {
            case ALL -> null;
            case MALE -> 0;
            case FEMALE -> 1;
        };
    }

    private boolean isMonthly(RankingType rankingType) {
        return rankingType.equals(RankingType.MONTHLY);
    }

    public String build() {
        StringBuilder sb = new StringBuilder(apiUrl);
        appendParam(sb, "startDt", periodStart.toString());
        appendParam(sb, "endDt", periodEnd.toString());

        appendParam(sb, "from_age", ageStart);
        appendParam(sb, "to_age", ageEnd);
        appendParam(sb, "gender", genderCode);

        return sb.toString();
    }

    private void appendParam(StringBuilder sb, String key, String val) {
        if (val == null)
            return;
        sb.append("&").append(key).append("=").append(val);
    }

    private void appendParam(StringBuilder sb, String key, Integer val) {
        if(val == null)
            return;
        sb.append("&").append(key).append("=").append(val);
    }
}
