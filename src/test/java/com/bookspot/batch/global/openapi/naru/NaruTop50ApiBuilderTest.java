package com.bookspot.batch.global.openapi.naru;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NaruTop50ApiBuilderTest {
    private static final LocalDate referenceDate = LocalDate.of(2025, 8, 15);
    private static final String baseUrl = "TEST_URL?key=AAA";

    @ParameterizedTest(name = "{1}")
    @MethodSource("args")
    void top50_api(String expectedUrl, RankingConditions conditions) {
        NaruTop50ApiBuilder builder = new NaruTop50ApiBuilder(baseUrl, referenceDate, conditions);

        assertEquals(
                expectedUrl,
                builder.build()
        );
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-01&endDt=2025-08-31"),
                        new RankingConditions(RankingType.MONTHLY, RankingGender.ALL, RankingAge.ALL)
                ),
                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-11&endDt=2025-08-17"),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.ALL)
                ),

                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-11&endDt=2025-08-17&gender=0"),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.MALE, RankingAge.ALL)
                ),
                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-11&endDt=2025-08-17&gender=1"),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.FEMALE, RankingAge.ALL)
                ),

                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-11&endDt=2025-08-17&from_age=0&to_age=14"),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.AGE_0_14)
                ),
                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-11&endDt=2025-08-17&from_age=20&to_age=29"),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.AGE_20_29)
                ),
                Arguments.of(
                        baseUrl.concat("&startDt=2025-08-11&endDt=2025-08-17&from_age=50"),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.AGE_50_UP)
                )
        );
    }



}