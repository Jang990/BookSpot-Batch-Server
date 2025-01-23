package com.bookspot.batch.data.crawler;

import java.time.LocalDate;
import java.util.Objects;

public record StockFileData(long libraryId, String filePath, LocalDate stockUpdatedAt) {
    public StockFileData {
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(stockUpdatedAt);
    }
}
