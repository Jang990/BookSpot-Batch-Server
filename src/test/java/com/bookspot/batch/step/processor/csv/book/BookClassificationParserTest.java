package com.bookspot.batch.step.processor.csv.book;

import com.bookspot.batch.data.file.csv.StockCsvData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BookClassificationParserTest {
    BookClassificationParser parser = new BookClassificationParser();

    @ParameterizedTest
    @MethodSource("args")
    void 책_분류코드의_의미있는_앞의_숫자파싱(String code, Integer expected) throws Exception {
        assertEquals(expected, parser.parsePrefix(code));
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of("25.11", 25),
                Arguments.of("5.123", 5),
                Arguments.of("123", 123),
                Arguments.of("345.678", 345),
                Arguments.of("901,123", 901), // 콤마까진 정상처리
                Arguments.of("ab901,123", null),
                Arguments.of("901?123", null),

                Arguments.of("", null),
                Arguments.of(null, null)

        );
    }

}