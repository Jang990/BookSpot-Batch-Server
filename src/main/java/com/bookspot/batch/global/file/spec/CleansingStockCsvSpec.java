package com.bookspot.batch.global.file.spec;

public enum CleansingStockCsvSpec {
    BOOK_ID("bookId"),
    LIBRARY_ID("libraryId"),
    SUBJECT_CODE("subjectCode");

    private final String fieldName;

    CleansingStockCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }

    public static String createLine(long bookId, long libraryId, String subjectCode) {
        return String.format("%d,%d,%s", bookId, libraryId, subjectCode == null ? "" : subjectCode);
    }
}
