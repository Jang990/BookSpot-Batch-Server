package com.bookspot.batch.global.file.spec.stock;

import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetadata;
import com.bookspot.batch.global.file.stock.StockCsvMetadataCreator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockCsvMetadataCreatorTest {

    @Test
    void 재고_파일_메타데이터를_반환() {
        FileMetadata metadata = StockCsvMetadataCreator.create(1L, LocalDate.of(2025, 1, 1));

        assertEquals(FileFormat.CSV, metadata.format());
        assertEquals("1_2025-01-01", metadata.name());
        assertEquals("1_2025-01-01.csv",metadata.fullName());
        assertEquals("bookSpotFiles/stock", metadata.directory());
        assertEquals("bookSpotFiles/stock/1_2025-01-01.csv", metadata.absolutePath());
    }

}