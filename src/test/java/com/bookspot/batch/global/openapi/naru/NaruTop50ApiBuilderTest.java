package com.bookspot.batch.global.openapi.naru;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NaruTop50ApiBuilderTest {
    @Test
    void 월간_top50_api() {
        LocalDate referenceDate = LocalDate.of(2025, 8, 15);
        RankingConditions conditions = new RankingConditions(
                RankingType.MONTHLY,
                RankingGender.ALL,
                RankingAge.ALL
        );

        NaruTop50ApiBuilder builder = new NaruTop50ApiBuilder("AAA", referenceDate, conditions);
        assertEquals(
                "AAA?format=json&startDt=2025-08-01&endDt=2025-08-31",
                builder.build()
        );
    }

}