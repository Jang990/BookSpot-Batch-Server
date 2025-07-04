package com.bookspot.batch.global.file.spec;

public enum CleansingStockCsvSpec {
    BOOK_ID("bookId"),
    LIBRARY_ID("libraryId");

    private final String fieldName;

    CleansingStockCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }

    public static String createLine(long bookId, long libraryId) {
        return String.format("%d,%d", bookId, libraryId);
    }
}
