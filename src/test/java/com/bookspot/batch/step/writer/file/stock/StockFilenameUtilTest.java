package com.bookspot.batch.step.writer.file.stock;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockFilenameUtilTest {

    @Test
    void 파일명_생성_가능() {
        assertEquals("123_0001-01-01",
                StockFilenameUtil.create(new StockFilenameElement(123L, LocalDate.of(1, 1, 1))));

        assertEquals("1223_2025-11-01",
                StockFilenameUtil.create(new StockFilenameElement(1223L, LocalDate.of(2025, 11, 1))));
    }

    @Test
    void 파일명_정보_파싱_가능() {
        assertEquals(new StockFilenameElement(123L, LocalDate.of(1, 1, 1)),
                StockFilenameUtil.parse("123_0001-01-01"));

        assertEquals(new StockFilenameElement(1223L, LocalDate.of(2025, 11, 1)),
                StockFilenameUtil.parse("1223_2025-11-01"));
    }

    @Test
    void 파일명에_확장자가_있어도_파싱_가능() {
        assertEquals(new StockFilenameElement(123L, LocalDate.of(1, 1, 1)),
                StockFilenameUtil.parse("123_0001-01-01.csv"));

        assertEquals(new StockFilenameElement(1223L, LocalDate.of(2025, 11, 1)),
                StockFilenameUtil.parse("1223_2025-11-01.csv"));
    }

}