package com.bookspot.batch.job.launcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalDateResolverTest {
    @Mock
    LocalDateHolder dateHolder;

    @InjectMocks
    LocalDateResolver resolver;

    @ParameterizedTest
    @MethodSource("args")
    void 이전날_중에_가장_가까준_월요일_반환(LocalDate expected, LocalDate referenceDate) {
        when(dateHolder.now()).thenReturn(referenceDate);

        LocalDate result = resolver.resolveMondayOfWeek();

        assertEquals(expected, result);
        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(
                        _2025_08(4),
                        _2025_08(5) // 화요일
                ),
                Arguments.of(
                        _2025_08(4),
                        _2025_08(6) // 수요일
                ),
                Arguments.of(
                        _2025_08(4),
                        _2025_08(7) // 목요일
                ),
                Arguments.of(
                        _2025_08(4),
                        _2025_08(8) // 금요일
                ),
                Arguments.of(
                        _2025_08(4),
                        _2025_08(9) // 토요일
                ),
                Arguments.of(
                        _2025_08(4),
                        _2025_08(10) // 일요일
                ),

                Arguments.of(
                        _2025_08(11),
                        _2025_08(11) // 월요일
                ),

                Arguments.of(
                        _2025_08(11),
                        _2025_08(12) // 화요일
                )
        );
    }

    static LocalDate _2025_08(int dayOfMonth) {
        return LocalDate.of(2025, 8, dayOfMonth);
    }

}