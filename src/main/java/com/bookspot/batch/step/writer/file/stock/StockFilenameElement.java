package com.bookspot.batch.step.writer.file.stock;

import java.time.LocalDate;

public record StockFilenameElement(long libraryId, LocalDate referenceDate) {
}
