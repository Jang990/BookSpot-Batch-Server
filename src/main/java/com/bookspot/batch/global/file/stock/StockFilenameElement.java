package com.bookspot.batch.global.file.stock;

import java.time.LocalDate;

public record StockFilenameElement(long libraryId, LocalDate referenceDate) {
}
