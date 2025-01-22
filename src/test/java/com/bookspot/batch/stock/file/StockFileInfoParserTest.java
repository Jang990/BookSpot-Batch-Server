package com.bookspot.batch.stock.file;

import com.bookspot.batch.global.crawler.naru.CsvFilePath;
import com.bookspot.batch.global.crawler.naru.NaruCrawler;
import com.bookspot.batch.stock.data.CurrentLibrary;
import com.bookspot.batch.stock.data.StockFileData;
import com.bookspot.batch.stock.processor.file.StockFileInfoParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockFileInfoParserTest {
    @Mock
    NaruCrawler naruCrawler;

    @InjectMocks
    StockFileInfoParser parser;

    @ParameterizedTest(name = "{3}")
    @MethodSource("args")
    void 날짜에_따라_파일_정보나_NULL을_반환한다(
            LocalDate stockUpdatedAt,
            LocalDate currentFileDate,
            LocalDate expected,
            String title) {
        when(naruCrawler.findCurrentBooksFilePath(any())).thenReturn(new CsvFilePath("FILE_PATH", currentFileDate));

        StockFileData result = parser.parse(new CurrentLibrary("123", "NARU_DETAIL", stockUpdatedAt));

        if(expected == null)
            assertNull(result);
        else
            assertEquals(expected, result.stockUpdatedAt());
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(null, createDate(1), createDate(1),
                        "재고 날짜가 NULL => 파일 정보 반환"),

                Arguments.of(createDate(1), createDate(2), createDate(2),
                        "재고 날짜 < 현재 파일 날짜 => 파일 정보 반환"),

                Arguments.of(createDate(2), createDate(2), null,
                        "재고 날짜 == 현재 파일 => null 반환"),

                Arguments.of(createDate(2), createDate(1), null,
                        "재고 날짜 > 현재 파일 날짜 => null 반환")
        );
    }

    @Test
    void 날짜에_따라_파싱한_파일_정보를_반환한다() {
        when(naruCrawler.findCurrentBooksFilePath(any())).thenReturn(
                new CsvFilePath("FILE_PATH", createDate(1)));

        StockFileData result = parser.parse(
                new CurrentLibrary("123", "NARU_DETAIL", null));

        assertEquals("123", result.libraryCode());
        assertEquals("FILE_PATH", result.filePath());
        assertEquals(createDate(1), result.stockUpdatedAt());
    }

    private static LocalDate createDate(int day) {
        return LocalDate.of(1, 1, day);
    }
}