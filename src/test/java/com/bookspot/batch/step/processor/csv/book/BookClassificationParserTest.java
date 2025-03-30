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
                Arguments.of(null, null),

                Arguments.of(".91", null), // StockCsvData(title=해든 분식, author=동지아 글;윤정주 그림, publisher=문학동네, publicationYear=2024, isbn=9791141607791, volume=52, subjectCode=.91, numberOfBooks=1, loanCount=2)
                                                    // StockCsvData(title=동물을 그리자, author=김충원 글, 그림, publisher=진선출판사, publicationYear=1999, isbn=9788972211631, volume=, subjectCode=.650, numberOfBooks=1, loanCount=78)
                Arguments.of(".388.3", null) // StockCsvData(title=도적이 줄줄줄, author=은현정 글 ;이은주 그림, publisher=한솔수북, publicationYear=2014, isbn=9791185494661, volume=, subjectCode=.388.3, numberOfBooks=1, loanCount=10)
        );
    }

}