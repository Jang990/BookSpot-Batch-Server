package com.bookspot.batch.stock;

import com.bookspot.batch.stock.data.LibraryStockCsvData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import static org.junit.jupiter.api.Assertions.*;

class StockStepConfigTest {
    StockStepConfig config = new StockStepConfig();

    private static final String TARGET_PATH = "부평구립삼산도서관 장서 대출목록 (2024년 12월).csv";
    private static final Resource SAMPLE_RESOURCE = new PathMatchingResourcePatternResolver().getResource(TARGET_PATH);

    @Test
    @DisplayName("파일 리딩 가능")
    void test1() throws Exception {
        FlatFileItemReader<LibraryStockCsvData> reader = config.bookStockCsvFileReader(SAMPLE_RESOURCE);
        reader.open(new ExecutionContext());

        assertEquals("9791171830602", reader.read().getIsbn());
        assertEquals("9791192431932", reader.read().getIsbn());
        assertEquals("9788954605816", reader.read().getIsbn());
        assertEquals("9788954605816", reader.read().getIsbn());
    }
}